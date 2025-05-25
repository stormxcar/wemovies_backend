package com.example.demo.services.Impls;

import com.example.demo.config.user.JwtUtil;
import com.example.demo.dto.*;
import com.example.demo.models.auth.Role;
import com.example.demo.models.auth.User;
import com.example.demo.models.auth.VerificationToken;
import com.example.demo.repositories.auth.RoleRepository;
import com.example.demo.repositories.auth.UserRepository;
import com.example.demo.repositories.auth.VerificationTokenRepository;
import com.example.demo.services.AuthService;
import com.example.demo.services.EmailService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;

    public AuthServiceImpl(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public AuthResponse login(LoginRequest loginRequest) {
        // Tìm user theo email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        // Kiểm tra tài khoản đã xác thực chưa
        if (!user.getIsActive()) {
            throw new RuntimeException("Tài khoản chưa được xác thực qua OTP");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(loginRequest.getPassWord(), user.getPassWord())) {
            throw new RuntimeException("Sai mật khẩu");
        }

        // Load UserDetails
        final UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        // Tạo Access Token và Refresh Token
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, user);
    }


    @Override
    public void requestOtp(RegisterRequest request) {
        // Kiểm tra domain email hợp lệ trước khi gửi OTP
        if (!request.getEmail().matches("^[a-zA-Z0-9._%+-]+@(gmail\\.com|yahoo\\.com|outlook\\.com)$")) {
            throw new IllegalArgumentException("Chỉ chấp nhận email từ gmail.com, yahoo.com hoặc outlook.com");
        }

        // Kiểm tra email đã tồn tại trong DB
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại");
        }

        // Kiểm tra email đã yêu cầu OTP trước đó chưa
        if (verificationTokenRepository.existsByEmailAndExpiryDateAfter(request.getEmail(), LocalDateTime.now())) {
            throw new IllegalArgumentException("Bạn đã yêu cầu OTP, vui lòng kiểm tra email hoặc đợi hết hạn");
        }

        // Tìm role (để sau xác thực dùng)
        Role role = roleRepository.findByRoleName(request.getRoleName())
                .orElseThrow(() -> new IllegalArgumentException("Role không tồn tại"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        ObjectMapper mapper = new ObjectMapper();
        String json;
        try {
            json = mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Lỗi khi xử lý dữ liệu người dùng", e);
        }

        VerificationToken token = new VerificationToken();

        token.setTempUserName(request.getUserName());
        token.setTempPassword(passwordEncoder.encode(request.getPassWord()));
        token.setFullName(request.getFullName());
        token.setEmail(request.getEmail());
        token.setPhoneNumber(request.getPhoneNumber());
        token.setRoleName(role.getRoleName());

        token.setOtp(otp);
        token.setUserData(json);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(1));
        verificationTokenRepository.save(token);

        String emailContent = "<p>OTP của bạn là: <strong>" + otp + "</strong></p>";
        try {
            emailService.sendEmail(request.getEmail(), "Mã OTP xác thực", emailContent);
        } catch (MailException e) {
            throw new IllegalArgumentException("Email không tồn tại hoặc không thể gửi email đến địa chỉ này");
        }
    }

    @Override
    public AuthResponse verifyOtp(String email, String otp) {
        VerificationToken token = verificationTokenRepository.findByEmailAndOtp(email, otp)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã OTP"));


        // Tạo user từ thông tin tạm
        User user = new User();
        user.setUserName(token.getTempUserName());
        user.setPassWord(token.getTempPassword());
        user.setFullName(token.getFullName());
        user.setEmail(token.getEmail());
        user.setPhoneNumber(token.getPhoneNumber());
        user.setIsActive(true);
        user.setCreateAt(LocalDateTime.now());

        Role role = roleRepository.findByRoleName(token.getRoleName())
                .orElseThrow(() -> new RuntimeException("Role không tồn tại"));
        user.setRole(role);

        userRepository.save(user);

        // Xóa token sau khi xác thực thành công
        verificationTokenRepository.delete(token);

        // Tạo token
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(user.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        return new AuthResponse(accessToken, refreshToken, user);
    }


    @Override
    public UserDetails verifyToken(String token) {
        String email = jwtUtil.extractUsername(token); // giả sử sub = email
        UserDetails userDetails = userDetailsService.loadUserByUsername(email); // dùng email để load
        if (email != null && jwtUtil.validateToken(token, userDetails)) {
            return userDetails;
        }
        throw new RuntimeException("Token không hợp lệ");
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        try {
            // Kiểm tra refresh token có hợp lệ không
            String username = jwtUtil.extractUsername(refreshToken);
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(refreshToken, userDetails)) {
                // Tạo access token mới
                String newAccessToken = jwtUtil.generateToken(userDetails);
                String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);

                return new AuthResponse(newAccessToken, newRefreshToken);
            }
        } catch (Exception e) {
            throw new RuntimeException("Làm mới token thất bại", e);
        }
        throw new RuntimeException("Refresh Token không hợp lệ");
    }

    @Override
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));

        String otp = String.format("%06d", new Random().nextInt(999999));

        VerificationToken token = new VerificationToken();
        token.setEmail(email);
        token.setOtp(otp);
        token.setExpiryDate(LocalDateTime.now().plusMinutes(3));
        verificationTokenRepository.save(token);

        String content = "<p>Mã OTP khôi phục mật khẩu của bạn là: <strong>" + otp + "</strong></p>";
        emailService.sendEmail(email, "Khôi phục mật khẩu", content);
    }

    @Override
    public void resetPassword(ResetPasswordRequest request) {
        VerificationToken token = verificationTokenRepository
                .findByEmailAndOtp(request.getEmail(), request.getOtp())
                .orElseThrow(() -> new RuntimeException("OTP không hợp lệ hoặc đã hết hạn"));

        if (token.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("OTP đã hết hạn");
        }

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // Xóa OTP sau khi dùng
        verificationTokenRepository.delete(token);
    }

    @Override
    public void changePassword(String usernameOrEmail, ChangePasswordRequest request) {
        // Tìm user theo username/email
        User user = userRepository.findByUserName(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Người dùng không tồn tại"));

        // So sánh mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassWord())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mật khẩu cũ không đúng");
        }

        // Gán mật khẩu mới đã mã hoá
        user.setPassWord(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
