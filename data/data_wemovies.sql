-- --------------------------------------------------------
-- Máy chủ:                      127.0.0.1
-- Server version:               11.2.3-MariaDB - mariadb.org binary distribution
-- Server OS:                    Win64
-- HeidiSQL Phiên bản:           12.6.0.6765
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


-- Dumping database structure for movies_web

-- Dumping structure for table movies_web.actor
CREATE TABLE IF NOT EXISTS `actor` (
  `actor_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`actor_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.actor: ~0 rows (approximately)

-- Dumping structure for table movies_web.admin
CREATE TABLE IF NOT EXISTS `admin` (
  `admin_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`admin_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.admin: ~1 rows (approximately)
INSERT INTO `admin` (`admin_id`, `email`, `password`, `role`, `username`) VALUES
	(1, 'nguyentruongan0610@gmail.com', 'admin', 'ADMIN', 'admin');

-- Dumping structure for table movies_web.category
CREATE TABLE IF NOT EXISTS `category` (
  `category_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`category_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.category: ~8 rows (approximately)
INSERT INTO `category` (`category_id`, `name`) VALUES
	(1, 'Phim Mới'),
	(2, 'Phim Bộ'),
	(3, 'Phim Lẻ'),
	(4, 'Phim Shows'),
	(5, 'Hoạt Hình'),
	(6, 'Sắp Chiếu'),
	(7, 'Hài Hước'),
	(9, 'Phim Chiếu Rạp');

-- Dumping structure for table movies_web.country
CREATE TABLE IF NOT EXISTS `country` (
  `country_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`country_id`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.country: ~35 rows (approximately)
INSERT INTO `country` (`country_id`, `name`) VALUES
	(1, 'Trung Quốc'),
	(2, 'Thái Lan'),
	(3, 'Hồng Kông'),
	(4, 'Pháp'),
	(5, 'Đức'),
	(6, 'Hà Lan'),
	(7, 'Mexico'),
	(8, 'Thụy Điển'),
	(9, 'Philippines'),
	(10, 'Đan Mạch'),
	(11, 'Thụy Sĩ'),
	(12, 'Ukraina'),
	(13, 'Hàn Quốc'),
	(14, 'Âu Mỹ'),
	(15, 'Ấn Độ'),
	(16, 'Canada'),
	(17, 'Tây Ban Nha'),
	(18, 'Indonesia'),
	(19, 'Ba lan'),
	(20, 'Malaysia'),
	(21, 'Bồ Đào Nha'),
	(22, 'UAE'),
	(23, 'Châu Phi'),
	(24, 'Ả Rập Xê Út'),
	(25, 'Nhật Bản'),
	(26, 'Đài Loan'),
	(27, 'Anh'),
	(28, 'Quốc Gia Khác'),
	(29, 'Thổ Nhĩ Kỳ'),
	(30, 'Nga'),
	(31, 'Úc'),
	(32, 'Brazil'),
	(33, 'Ý'),
	(34, 'Na Uy'),
	(35, 'Nam Phi');

-- Dumping structure for table movies_web.movie
CREATE TABLE IF NOT EXISTS `movie` (
  `movie_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` longtext DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  `release_year` int(11) DEFAULT NULL,
  `thumb_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `country_id` bigint(20) DEFAULT NULL,
  `title_by_language` varchar(255) DEFAULT NULL,
  `director` varchar(255) DEFAULT NULL,
  `duration` int(11) DEFAULT NULL,
  `quality` tinyint(4) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `viet_sub` bit(1) NOT NULL,
  `views` bigint(20) NOT NULL,
  `trailer` varchar(255) DEFAULT NULL,
  `total_episodes` int(11) DEFAULT NULL,
  `episode_links` longtext DEFAULT NULL,
  `hot` bit(1) NOT NULL,
  PRIMARY KEY (`movie_id`),
  KEY `FK5h5hkyxprvsgpqg69nqsq5p48` (`country_id`),
  CONSTRAINT `FK5h5hkyxprvsgpqg69nqsq5p48` FOREIGN KEY (`country_id`) REFERENCES `country` (`country_id`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.movie: ~21 rows (approximately)
INSERT INTO `movie` (`movie_id`, `description`, `link`, `release_year`, `thumb_url`, `title`, `country_id`, `title_by_language`, `director`, `duration`, `quality`, `status`, `viet_sub`, `views`, `trailer`, `total_episodes`, `episode_links`, `hot`) VALUES
	(2, 'Sau khi tình trạng hồ yêu hỗn loạn lắng xuống, kinh thành đã trở lại thịnh vượng và nhộn nhịp như trước đây, Vũ hoàng hậu có tâm trạng tốt và quyết định tổ chức một cuộc đi săn mùa thu thật hoành tráng, mời các hoàng tử, quý tộc và những người giỏi võ thuật tụ tập lại để thi đấu trên lưng ngựa và bắn cung. Mộ Dung Kỷ, khanh trẻ của Thổ Cốc Hỗn, cũng được mời tham gia cuộ đi săn mùa thu này, nhưng vô tình trở thành "nghi phạm" trong âm mưu ám sát hoàng hậu. Mộ Dung Kỷ không còn cách nào khác ngoài nhờ Bồi Khôn giúp đỡ, hy vọng anh có thể giúp tìm ra kẻ sát nhân thực sự và làm rõ danh tính của hắn. Để bảo vệ hòa bình của nhà Đường và nhân dân, đồng thời giúp đỡ bạn bè của mình, Bồi Khôn, Úy Trì Trụ, Hàn Đông Thanh và các cộng sự khác một lần nữa hợp lực điều tra vụ án và cuối cùng thủ phạm thực sự lên kế hoạch ám sát Vũ Hoàng hậu đã bị truy tìm, cuộc chiến giữa nhà Đường và Thổ Cốc Hỗn đã bị ngăn chặn.', 'https://vip.opstream11.com/share/e2bfca67f4a5745fd1e386afd295017b', 2023, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1735297367/thumb_webFilm/phong-khoi-lac-duong-song-tu-truy-hung-thumb_oaf6co.avif', 'Phong Khởi Lạc Dương: Song Tử Truy Hung', 1, 'Gemini Mission', 'Dan Xie', 67, 1, 'full', b'1', 0, 'https://youtu.be/jmxDVWJBvgQ?si=qPoU26R8A97Npuyw', NULL, NULL, b'0'),
	(3, 'Sergei Kravinoff (Aaron Taylor-Johnson thủ vai) cùng bố của anh đi săn ở Châu Phi. Bố của Sergei Kravinoff là một tay săn lão luyện. Khi Sergei bị một con sư tử tấn công, bố của anh cho rằng anh chỉ là một kẻ yếu đuối. Mặc dù bị sư tử cắn và bị chính bố mình bỏ mặc, nhưng Sergei đã không chết mà thay vào đó, vết cắn lại cho Sergei một sức mạnh phi thường. Cũng từ đây, gã nhập cư Sergei Kravinoff đang thực hiện nhiệm vụ chứng minh rằng anh ta là thợ săn vĩ đại nhất thế giới.', 'https://vip.opstream15.com/share/8f2e65c2d5797956636c41203cf99426', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1735297367/thumb_webFilm/kraven-tho-san-thu-linh-thumb_urksmb.avif', 'Kraven: Thợ Săn Thủ Lĩnh', 14, 'Kraven the Hunter', 'J.C. Chandor', 127, 1, 'full', b'1', 0, 'https://youtu.be/Kng73ZSbehc?si=B7VmbrJpYOVj8Edj', NULL, NULL, b'1'),
	(4, 'Trong khi thảo luận về tương lai của trang trại ô liu bình dị của họ vào bữa ăn, sự phức tạp trong mối quan hệ của gia đình nọ hiện rõ qua những tiếng cười và những lời thú nhận.', 'https://vip.opstream11.com/share/33702a9c691c0f5aaac103d7dd1952eb', 2023, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1735297511/thumb_webFilm/familia-thumb_p392ao.avif', 'Familia', 7, 'Familia', 'Rodrigo García', 105, 1, 'full', b'1', 0, 'https://youtu.be/I5B2U9yJg7U?si=bCauqjXzVpXXZn6a', NULL, NULL, b'0'),
	(5, '“Hành Trình của Moana 2” là màn tái hợp của Moana và Maui sau 3 năm, trở lại trong chuyến phiêu lưu cùng với những thành viên mới. Theo tiếng gọi của tổ tiên, Moana sẽ tham gia cuộc hành trình đến những vùng biển xa xôi của Châu Đại Dương và sẽ đi tới vùng biển nguy hiểm, đã mất tích từ lâu. Cùng chờ đón cuộc phiêu lưu của Moana đầy chông gai sắp tới nhé.', 'https://vip.opstream15.com/share/6e65e50146bf86839d4a52493163959a', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1735618304/thumb_webFilm/hanh-trinh-cua-moana-2-thumb_tdjc80.avif', 'Hành Trình Của Moana 2', 14, 'Moana 2', 'David G. Derrick Jr., Jason Hand', 100, 1, 'full', b'1', 0, 'https://youtu.be/hDZ7y8RP5HE?si=indb9U0xywNQ9_IH', NULL, NULL, b'1'),
	(6, 'Khi một bùa chú mạnh mẽ biến cha mẹ cô thành quái vật khổng lồ, nàng công chúa tuổi teen nọ phải dấn thân vào nơi hoang dã để phá bỏ lời nguyền trước khi quá muộn.', 'https://vip.opstream11.com/share/1e0b98ffbf6e647bbcfb5f1032f21ce5', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1735626530/thumb_webFilm/spellbound-chuyen-phieu-luu-phep-thuat-thumb_jxbhyw.avif', 'Spellbound: Chuyến phiêu lưu phép thuật', 14, 'Spellbound', 'Vicky Jenson', 106, 1, 'full', b'1', 0, 'https://youtu.be/jGQiq1ZuCW8?si=xkz9zBt1i9jW1DzK', NULL, NULL, b'1'),
	(7, 'Chuyển thể từ truyền thuyết dân gian, kể về một vụ việc kỳ bí xảy ra tại vùng đất Ninh Châu, khi quái vật đang quấy phá, Địch Nhân Kiệt sau khi biết được chuyện này đã dẫn theo trợ thủ tiến hành điều tra bí mật lại phát hiện ra một vụ án tham nhũng không ai biết đến.', 'https://vip.opstream17.com/share/3b2f3a493d32e9aca1df90ef35b587e7', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736496293/thumb_webFilm/dich-nhan-kiet-dai-huyen-thuat-su-thumb_oflnls.avif', 'Địch Nhân Kiệt: Đại Huyễn Thuật Sư', 1, 'Detective Dee and the Great Illusionist Class	', '郭绍恒 Shaoheng Guo', 70, 1, 'full', b'1', 0, 'https://youtu.be/_QCJkxc0-b0?si=PJpZR_xlH8PMPhQT', NULL, NULL, b'0'),
	(8, 'Tình yêu giữa Noah và Nick dường như không hề lay chuyển mặc dù cha mẹ họ đã cố gắng chia cắt họ. Nhưng công việc của anh và việc cô vào đại học đã mở ra cuộc sống của họ với những mối quan hệ mới sẽ làm lung lay nền tảng của cả mối quan hệ của họ và gia đình Leister.', 'https://vip.opstream10.com/share/e74843b99da8b29775c6aa9080436844', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736496471/thumb_webFilm/loi-tai-anh-thumb_qtxcts.avif', 'Lỗi Tại Anh (Vietsub)', 14, 'Your Fault', 'Domingo González', 120, 1, 'full', b'1', 0, 'https://youtu.be/sOmBXymF3Rk?si=IqbxX8SABX6VHPkQ', NULL, NULL, b'0'),
	(9, 'Vào hậu trường cùng đạo diễn Zack Snyder, dàn diễn viên và đoàn làm phim của trường ca hoành tráng này khi họ đưa một vũ trụ khoa học viễn tưởng mới rộng lớn lên màn ảnh.', 'https://vip.opstream90.com/share/0bb4aec1710521c12ee76289d9440817', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736496646/thumb_webFilm/tao-nen-mot-vu-tru-hau-truong-rebel-moon-thumb_bhyytq.avif', 'Tạo nên một vũ trụ - Hậu trường Rebel Moon', 14, 'Creating a Universe - The Making of Rebel Moon', 'Banks Farris, Jon Deaton', 28, 1, 'full', b'1', 0, 'https://youtu.be/MGwEvYxpCWY?si=sxneNvWH8j6cu4mR', NULL, NULL, b'0'),
	(10, 'Câu chuyện về việc đoàn tụ với người thân đã khuất hoặc người yêu qua cuộc gọi video.', 'https://vip.opstream13.com/share/983a33a9a86796df362c1108e00f54a6', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736496798/thumb_webFilm/xu-so-than-tien-2024-thumb_nbvpxl.avif', 'Xứ Sở Thần Tiên (Vietsub)', 13, 'Wonderland (2024)', '김태용', 120, 1, 'full', b'1', 0, 'https://youtu.be/NFIRWIGxWl8?si=QNxRSESgVRGMURGy', NULL, NULL, b'0'),
	(11, 'Gượng gạo Thiểm Tây-陕囧 (2017) [HD-Vietsub]', 'https://vip.opstream11.com/share/fa83a11a198d5a7f0bf77a1987bcd006', 2017, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736496919/thumb_webFilm/guong-gao-thiem-tay-thumb_gzi9bp.avif', 'Gượng gạo Thiểm Tây', 1, '陕囧', 'Đang cập nhật', 74, 1, 'full', b'1', 0, '', NULL, NULL, b'0'),
	(13, 'Một cảnh sát ở Singapore điều tra sự biến mất của một công nhân xây dựng nhập cư Trung Quốc, người này đã không ngủ nhiều đêm để chơi một trò chơi điện tử bí ẩn.', 'https://vip.opstream17.com/share/5ae81daa87d6649df09002741e5b1738', 2019, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736497128/thumb_webFilm/vung-dat-tuong-tuong-thumb_lulfk5.avif', 'Vùng đất tưởng tượng', 30, 'A Land Imagined', 'Đang cập nhật', 95, 1, 'full', b'1', 0, 'https://youtu.be/UKRLkJBrP0s?si=4iWkIeAxS2nqmCx8', NULL, NULL, b'0'),
	(14, 'Mufasa: Vua Sư Tử là phần tiền truyện của bộ phim hoạt hình Vua Sư Tử trứ danh, kể về cuộc đời của Mufasa - cha của Simba. Phim là hành trình Mufasa từ một chú sư tử mồ côi lạc đàn trở thành vị vua sư tử huyền thoại của Xứ Vua (Pride Land). Ngoài ra, quá khứ về tên phản diện Scar và hành trình hắc hóa của hắn cũng sẽ được phơi bày trong phần phim này.', 'https://vip.opstream15.com/share/264fd6698e2c38e1c7586813338a09fe', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736497253/thumb_webFilm/mufasa-vua-su-tu-thumb_oxdrb2.avif', 'Mufasa: Vua Sư Tử', 1, 'Mufasa: The Lion King', 'Barry Jenkins', 118, 1, 'full', b'0', 0, 'https://youtu.be/lMXh6vjiZrI?si=LxU4kx9uiaspcDpe', NULL, NULL, b'1'),
	(15, 'Chú chó xuất chúng Gromit gấp rút cứu chủ nhân khi phát minh công nghệ cao của Wallace bổng nổi loạn và anh bị đổ oan thực hiện hàng loạt tội ác đáng ngờ.', 'https://vip.opstream15.com/share/e1bc55fb92b356f04a9885d51edd0fa2', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736497411/thumb_webFilm/wallace-va-gromit-long-vu-bao-thu-thumb_co0e4r.avif', 'Wallace và Gromit: Lông vũ báo thù', 27, 'Wallace & Gromit: Vengeance Most Fowl Class	', 'Đang cập nhật', 79, 1, 'full', b'0', 0, 'https://youtu.be/X0ZGf2B01uU?si=9zml5JtM4HGQRmMT', NULL, NULL, b'0'),
	(17, 'Hàng trăm người chơi kẹt tiền chấp nhận một lời mời kỳ lạ: thi đấu trong các trò chơi trẻ con. Đón chờ họ là một giải thưởng hấp dẫn – và những rủi ro chết người.', NULL, 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736497592/thumb_webFilm/tro-choi-con-muc-phan-2-thumb_ulyazh.avif', 'Trò chơi con mực (Phần 2)', 13, 'Squid Game (Season 2)', 'Đang cập nhật', 56, 1, '7/7', b'1', 0, 'https://youtu.be/Ed1sGgHUo88?si=tflvQ52v0hP0-xf2', 7, 'https://vip.opstream12.com/share/41843cfffdb6bda9553124b20718d246,https://vip.opstream12.com/share/92e7055a62fe019df970d4258b33d92e,https://vip.opstream16.com/share/403e7163b5aef0323eee42fe413bccc5,https://vip.opstream12.com/share/77da6346955af7cc9c69c1003a412e8a,https://vip.opstream12.com/share/ebdbfa1c3de4d826bbe7fe360c211ecc,https://vip.opstream12.com/share/1c107b00d900d3fabc39ecf95a0b2d0e,https://vip.opstream12.com/share/dd7c7a7355c7fa31bb876bd9fc3fffae', b'1'),
	(18, 'Khi vụ giết người kép kinh hoàng không được phá giải trong suốt 16 năm, viên thanh tra nọ bắt tay với một nhà phả hệ học để bắt hung thủ trước khi vụ án trở thành án treo.', '', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736498676/thumb_webFilm/dot-pha-quyet-dinh-thumb_slclzn.avif', 'Đột phá quyết định', 8, 'The Breakthrough', 'Đang cập nhật', 40, 1, '4/4', b'1', 0, 'https://youtu.be/dJ1hi8VAhp8?si=m5_knCIqAYRErJnO', 4, 'https://vip.opstream15.com/share/b73ed4d64cca3fb0e022a0204ec16f4e,https://vip.opstream15.com/share/702c7c3c2936d1b5a651c1cd184b2e7f,https://vip.opstream15.com/share/4434510da6f777ce53a98936d874b600,https://vip.opstream15.com/share/d04beca1616f92edb95fe79c395756fa', b'0'),
	(20, 'Đảo Amami Oshima-Amami Ashima Island (2020) [HD-Vietsub]', 'https://vip.opstream17.com/share/24aef8cb3281a2422a59b51659f1ad2e', 2020, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736498999/thumb_webFilm/dao-amami-oshima-thumb_ug6l5x.avif', 'Đảo Amami Oshima', 25, 'Amami Ashima Island', 'Đang cập nhật', 55, 1, 'full', b'1', 0, 'https://youtu.be/M1RmxLOsLdE?si=Vmb2wtGJfLa-b9JY', NULL, NULL, b'0'),
	(22, 'Trong bộ phim tài liệu này, doanh nhân giàu có Bryan Johnson mạo hiểm cả sức khỏe và tài sản để thách thức quá trình lão hóa và kéo dài tuổi thọ vượt qua mọi giới hạn đã biết.', 'https://vip.opstream15.com/share/2d4ed3b30fbbe86b239601924f8d89d4', 2025, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736498542/thumb_webFilm/bryan-johnson-nguoi-khao-khat-truong-sinh-thumb_mi5gsl.avif', 'Bryan Johnson: Người Khao Khát Trường Sinh', 14, 'Don\'t Die: The Man Who Wants to Live Forever Class	', 'Đang cập nhật', 88, 1, 'full', b'1', 0, 'https://youtu.be/G6kBGIm9JwQ?si=gu6ihO5Mgp8Oyrto', NULL, NULL, b'0'),
	(23, 'Tình Người Duyên Ma - Tái Hợp Kang Mak from Pee Mak 2024 là phiên bản làm lại từ bộ phim hài – kinh dị nổi tiếng của Thái Lan "Pee Mak – Tình Người Duyên Ma". Phim xoay quanh Makmur một người lính Indonesia phải rời xa người vợ đang mang thai là Sari, để tham gia chiến tranh bảo vệ đất nước. Sau thời gian chiến đấu cùng các đồng đội anh trở về nhà và đoàn tụ với Sari, cô đã sinh con trong thời gian anh đi chiến đấu. Tuy nhiên những tin đồn trong làng cho rằng Sari đã qua đời và hiện chỉ là một hồn ma. Makmur và các chiến hữu của anh phải đối mặt với những tình huống dở khóc dở cười khi tìm cách giải quyết bí ẩn này.', 'https://vip.opstream15.com/share/6c5aa72f3b82fae8df860ec9d248bd99', 2024, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736498442/thumb_webFilm/tinh-nguoi-duyen-ma-tai-hop-thumb_mp3otk.avif', 'Tình Người Duyên Ma: Tái Hợp', 18, 'Kang Mak (From Pee Mak)', 'Đang cập nhật', 122, 1, 'full', b'1', 0, 'https://youtu.be/D5qofC6JZZk?si=JdZDt4tQ8iT8WRvn', NULL, NULL, b'1'),
	(25, 'A pair of high-frequency traders go up against their old boss in an effort to make millions in a fiber-optic cable deal.', 'https://vip.opstream15.com/share/1fc405aac65b7391cf82c1f213ceb6c6', 2019, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736499139/thumb_webFilm/du-an-chim-ruoi-thumb_yt0mem.avif', 'Dự Án Chim Ruồi', 16, 'The Hummingbird Project', 'Kim Nguyen', 111, 1, 'full', b'1', 0, 'https://youtu.be/tfnUI_1APCk?si=MZRX0qIlANF7mf5H', NULL, NULL, b'0'),
	(26, 'Cú Máy Ăn Tiền lấy bối cảnh thực tế và câu chuyện làm phim những năm 1970 ở Hàn Quốc. Kim Yeol (Song Kang Ho thủ vai) - một đạo diễn điện ảnh có bộ phim đầu tay được giới phê bình khen ngợi, nhưng sự nghiệp của ông tuột dốc không phanh khi liên tiếp ra đời những tác phẩm bị coi là “phim rác”. Sau khi hoàn thành xong bộ phim mới nhất là Cobweb, đạo diễn Kim cảm thấy cần quay lại cái kết để có thể tạo ra một kiệt tác. Tuy nhiên, kịch bản mới không vượt qua được khâu kiểm duyệt và các diễn viên cũng không thể hiểu được cái kết mới của ông. Giữa lịch trình rối rắm, sự phản đối từ nhà sản xuất, sự can thiệp của cơ quan kiểm duyệt và những mâu thuẫn đang xung đột trước mắt khiến đạo diễn Kim như muốn phát điên, nhưng ông vẫn tiếp tục một cách bất chấp: “Nếu tôi có thể thay đổi cái kết, một kiệt tác sẽ xuất hiện. Tất cả những gì tôi cần là 2 ngày”.', 'https://vip.opstream15.com/share/6a11646e982284c18c05ffd73dd14bdc', 2023, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736499229/thumb_webFilm/cu-may-an-tien-thumb_f5v36f.avif', 'Cú Máy Ăn Tiền', 13, 'Cobweb', '김지운', 135, 1, 'full', b'1', 0, 'https://youtu.be/9s3klPW3KGc?si=Z1WUSa0gcjgX1yQM', NULL, NULL, b'0'),
	(27, '<p><strong>AD VITAM</strong></p>\r\n\r\n<p><em>Khi anh v&agrave; người vợ đang mang thai bị tấn c&ocirc;ng tại nh&agrave;, cựu đặc vụ ưu t&uacute; nọ mắc kẹt trong cuộc săn người đầy chết ch&oacute;c li&ecirc;n quan đến qu&aacute; khứ đau thương của ch&iacute;nh anh.</em></p>\r\n\r\n<p>&nbsp;</p>\r\n', 'https://vip.opstream15.com/share/6c489ad0aa78ee6e196e8406aea36aae', 2023, 'https://res.cloudinary.com/dzwjgfd7t/image/upload/v1736592886/thumb_webFilm/ad-vitam-tron-doi-thumb_g3vvio.avif', 'Ad Vitam: Trọn Đời', 4, 'Ad Vitam Class	', 'Rodolphe Lauga', 97, 1, 'Full', b'1', 0, 'https://youtu.be/gMmF9uWqzK8?si=rQxwZeys1vN0MYGf', NULL, NULL, b'0');

-- Dumping structure for table movies_web.movie_actors
CREATE TABLE IF NOT EXISTS `movie_actors` (
  `movie_id` bigint(20) NOT NULL,
  `actor` varchar(255) DEFAULT NULL,
  KEY `FKbsto8yef4btokhveihmkg8876` (`movie_id`),
  CONSTRAINT `FKbsto8yef4btokhveihmkg8876` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.movie_actors: ~123 rows (approximately)
INSERT INTO `movie_actors` (`movie_id`, `actor`) VALUES
	(2, 'Dan Xie'),
	(3, 'Levi Miller'),
	(3, 'Diaana Babnicova'),
	(3, 'Fred Hechinger'),
	(3, 'Billy Barratt'),
	(3, 'Russell Crowe'),
	(3, 'Dritan Kastrati'),
	(3, 'Alessandro Nivola'),
	(3, 'Aaron Taylor-Johnson'),
	(3, 'Christopher Abbott'),
	(3, 'Ariana DeBose'),
	(4, 'Ilse Salas'),
	(4, 'Ricardo Selmen'),
	(4, 'Natalia Solián'),
	(4, 'Ángeles Cruz'),
	(4, 'Maribel Verdú'),
	(4, 'Brian Shortall'),
	(4, 'Daniel Giménez Cacho'),
	(4, 'Natalia Plascencia'),
	(4, 'Adolfo Madera'),
	(4, 'Cassandra Ciangherotti'),
	(5, 'Khaleesi Lambert-Tsuda'),
	(5, 'Auli\'i Cravalho'),
	(5, 'Awhimai Fraser'),
	(5, 'Hualālai Chung'),
	(5, 'Rose Matafeo'),
	(5, 'Nicole Scherzinger'),
	(5, 'Dwayne Johnson'),
	(5, 'Rachel House'),
	(5, 'Temuera Morrison'),
	(5, 'David Fane'),
	(6, 'Javier Bardem'),
	(6, 'Jenifer Lewis'),
	(6, 'Tituss Burgess'),
	(6, 'Nicole Kidman'),
	(6, 'John Ratzenberger'),
	(6, 'John Lithgow'),
	(6, 'Rachel Zegler'),
	(6, 'Nathan Lane'),
	(7, 'Zhou Ting'),
	(7, 'Kenneth Zhu'),
	(7, '周晴'),
	(7, 'Jiao Haihua'),
	(8, 'Gabriela Andrada'),
	(8, 'Iván Sánchez'),
	(8, 'Eva Ruiz'),
	(8, 'Marta Hazas'),
	(8, 'Alex Bejar'),
	(8, 'Javier Morgade'),
	(8, 'Felipe Londoño'),
	(8, 'Víctor Varona'),
	(8, 'Gabriel Guevara'),
	(8, 'Nicole Wallace'),
	(9, 'Charlotte Maggi'),
	(9, 'Elise Duffy'),
	(9, 'Dustin Ceithamer'),
	(9, 'Staz Nair'),
	(9, 'Djimon Hounsou'),
	(9, 'Brad Elliott'),
	(9, 'Sofia Boutella'),
	(9, 'Michiel Huisman'),
	(9, 'Anthony Hopkins'),
	(10, '배수지'),
	(10, '최우식'),
	(10, '박보검'),
	(10, 'Jung Yoo Mi'),
	(10, '湯唯'),
	(11, 'Đang cập nhật'),
	(13, 'Lưu Hiểu Nghĩa'),
	(13, 'Hoành Vinh'),
	(13, 'Quách Nguyệt'),
	(14, 'Mads Mikkelsen'),
	(14, 'Lennie James'),
	(14, 'Anika Noni Rose'),
	(14, 'Keith David'),
	(14, 'Preston Nyman'),
	(14, 'Kagiso Lediga'),
	(14, 'Thandiwe Newton'),
	(14, 'Kelvin Harrison Jr.'),
	(14, 'Aaron Pierre'),
	(14, 'Tiffany Boone'),
	(15, 'Đang cập nhật'),
	(17, '이진욱'),
	(17, '이서환'),
	(17, '강하늘'),
	(17, '조유리'),
	(17, '양동근'),
	(17, '위하준'),
	(17, '임시완'),
	(17, '이병헌'),
	(17, '이정재'),
	(17, '강애심'),
	(23, 'Đang cập nhật'),
	(22, 'Đang cập nhật'),
	(18, 'Đang cập nhật'),
	(20, 'Đang cập nhật'),
	(25, 'Kaniehtiio Horn'),
	(25, 'Jesse Eisenberg'),
	(25, 'Michael Mando'),
	(25, 'Johan Heldenbergh'),
	(25, 'Salma Hayek Pinault'),
	(25, 'Mark Slacke'),
	(25, 'Alexander Skarsgård'),
	(25, 'Frank Schorpion'),
	(25, 'Ayisha Issa'),
	(25, 'Sarah Goldberg'),
	(26, '정우성'),
	(26, '염혜란'),
	(26, '박정수'),
	(26, '임수정'),
	(26, '정수정'),
	(26, '오정세'),
	(26, '전여빈'),
	(26, '송강호'),
	(26, 'Um Tae-goo'),
	(26, '장영남'),
	(27, 'Etienne Guillou-Kervern'),
	(27, 'Johan Heldenbergh'),
	(27, 'Alexis Manenti'),
	(27, 'Nassim Lyes'),
	(27, 'Stéphane Caillard'),
	(27, 'Guillaume Canet'),
	(27, 'Zita Hanrot');

-- Dumping structure for table movies_web.movie_category
CREATE TABLE IF NOT EXISTS `movie_category` (
  `movie_id` bigint(20) NOT NULL,
  `category_id` bigint(20) NOT NULL,
  PRIMARY KEY (`movie_id`,`category_id`),
  KEY `FKhkem46gi7yq1019e1j8hlvp9y` (`category_id`),
  CONSTRAINT `FKdhlw8bp2rx2bhkp1orkg12lor` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`movie_id`),
  CONSTRAINT `FKhkem46gi7yq1019e1j8hlvp9y` FOREIGN KEY (`category_id`) REFERENCES `category` (`category_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.movie_category: ~25 rows (approximately)
INSERT INTO `movie_category` (`movie_id`, `category_id`) VALUES
	(2, 1),
	(25, 1),
	(17, 2),
	(18, 2),
	(3, 3),
	(4, 3),
	(7, 3),
	(8, 3),
	(9, 3),
	(10, 3),
	(11, 3),
	(13, 3),
	(14, 3),
	(20, 3),
	(22, 3),
	(23, 3),
	(25, 3),
	(26, 3),
	(27, 3),
	(2, 5),
	(5, 5),
	(6, 5),
	(14, 5),
	(15, 5),
	(26, 7);

-- Dumping structure for table movies_web.movie_genre
CREATE TABLE IF NOT EXISTS `movie_genre` (
  `movie_id` bigint(20) NOT NULL,
  `movie_type_id` bigint(20) NOT NULL,
  PRIMARY KEY (`movie_id`,`movie_type_id`),
  KEY `FKhxbsrrqnl3qtitfd71xqaryo3` (`movie_type_id`),
  CONSTRAINT `FKhxbsrrqnl3qtitfd71xqaryo3` FOREIGN KEY (`movie_type_id`) REFERENCES `movie_type` (`movie_type_id`),
  CONSTRAINT `FKp6vjabv2e2435at1hnuxg64yv` FOREIGN KEY (`movie_id`) REFERENCES `movie` (`movie_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.movie_genre: ~44 rows (approximately)
INSERT INTO `movie_genre` (`movie_id`, `movie_type_id`) VALUES
	(3, 1),
	(7, 1),
	(17, 1),
	(27, 1),
	(10, 4),
	(23, 5),
	(9, 6),
	(20, 6),
	(22, 6),
	(7, 7),
	(13, 7),
	(17, 7),
	(11, 9),
	(23, 9),
	(11, 10),
	(2, 12),
	(3, 12),
	(5, 12),
	(6, 12),
	(14, 12),
	(17, 12),
	(5, 14),
	(6, 14),
	(10, 14),
	(11, 14),
	(14, 14),
	(15, 14),
	(5, 16),
	(6, 16),
	(23, 16),
	(26, 16),
	(11, 17),
	(27, 17),
	(2, 18),
	(10, 19),
	(4, 21),
	(8, 21),
	(13, 21),
	(14, 21),
	(17, 21),
	(18, 21),
	(25, 21),
	(26, 21),
	(27, 21);

-- Dumping structure for table movies_web.movie_type
CREATE TABLE IF NOT EXISTS `movie_type` (
  `movie_type_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`movie_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.movie_type: ~22 rows (approximately)
INSERT INTO `movie_type` (`movie_type_id`, `type_name`) VALUES
	(1, 'Hành Động'),
	(2, 'Cổ Trang'),
	(3, 'Chiến Tranh'),
	(4, 'Viễn Tưởng'),
	(5, 'Kinh Dị'),
	(6, 'Tài Liệu'),
	(7, 'Bí ẩn'),
	(9, 'Tình Cảm'),
	(10, 'Tâm Lý'),
	(11, 'Thể Thao'),
	(12, 'Phiêu Lưu'),
	(13, 'Âm Nhạc'),
	(14, 'Gia Đình'),
	(15, 'Học Đường'),
	(16, 'Hài Hước'),
	(17, 'Hình Sự'),
	(18, 'Võ Thuật'),
	(19, 'Khoa Học'),
	(20, 'Thần Thoại'),
	(21, 'Chính kịch'),
	(22, 'Kinh Điển'),
	(24, 'Phim 18+');

-- Dumping structure for table movies_web.refresh_token
CREATE TABLE IF NOT EXISTS `refresh_token` (
  `token_id` int(11) NOT NULL AUTO_INCREMENT,
  `expiration_date` datetime(6) NOT NULL,
  `refresh_token` varchar(500) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `user_user_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`token_id`),
  UNIQUE KEY `UKf95ixxe7pa48ryn1awmh2evt7` (`user_id`),
  UNIQUE KEY `UKmw99w2d9yrljeaowdl0siv3e3` (`user_user_id`),
  CONSTRAINT `FKjtx87i0jvq2svedphegvdwcuy` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.refresh_token: ~0 rows (approximately)

-- Dumping structure for table movies_web.user
CREATE TABLE IF NOT EXISTS `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `password` varchar(255) DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.user: ~0 rows (approximately)

-- Dumping structure for table movies_web.users
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `role` enum('ADMIN','USER') DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `is_account_non_expired` bit(1) NOT NULL,
  `is_account_non_locked` bit(1) NOT NULL,
  `is_credentials_non_expired` bit(1) NOT NULL,
  `is_enabled` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- Dumping data for table movies_web.users: ~1 rows (approximately)
INSERT INTO `users` (`id`, `email`, `password`, `role`, `username`, `is_account_non_expired`, `is_account_non_locked`, `is_credentials_non_expired`, `is_enabled`, `name`) VALUES
	(1, 'nkha3513@gmai.com', '$2a$10$CjsdB9/nN8HOz7RuKaRQUeoE5r/K4Hgig7WfMtuuWdaPFSbRBV10W', 'USER', 'kha', b'1', b'1', b'1', b'1', 'Kha');

/*!40103 SET TIME_ZONE=IFNULL(@OLD_TIME_ZONE, 'system') */;
/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IFNULL(@OLD_FOREIGN_KEY_CHECKS, 1) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40111 SET SQL_NOTES=IFNULL(@OLD_SQL_NOTES, 1) */;
