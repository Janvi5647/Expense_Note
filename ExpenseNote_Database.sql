-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Sep 01, 2024 at 10:48 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `expensenote`
--

DELIMITER $$
--
-- Procedures
--
CREATE DEFINER=`root`@`localhost` PROCEDURE `addExpense` (IN `dates` DATE, IN `details` VARCHAR(30), IN `amounts` DOUBLE, IN `modes` VARCHAR(30), IN `id` INT, IN `c_id` INT, IN `time` TIMESTAMP)   BEGIN
	INSERT INTO expense ( date, detail, amount, mode,user_id,cid,timeStamp) VALUES ( dates, details, amounts, modes,id,c_id,time);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `addIncome` (IN `dates` DATE, IN `details` VARCHAR(30), IN `amounts` DOUBLE, IN `modes` VARCHAR(30), IN `id` INT, IN `time` TIMESTAMP)   BEGIN
	INSERT INTO income ( date, detail, amount, mode,user_id,timeStamp) VALUES ( dates, details, amounts, modes,id,time);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `checkMobileSingleuser` (OUT `mobile` VARCHAR(10), IN `userMobile` VARCHAR(10))   BEGIN
SELECT mobile_no into mobile from single_user where mobile_no=userMobile;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteExpense` (IN `id` INT(10))   BEGIN
DELETE FROM expense WHERE expense_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `deleteIncome` (IN `id` INT)   BEGIN
DELETE FROM income WHERE income_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `insertIntoSingleUser` (IN `name` VARCHAR(30), IN `mobile` VARCHAR(10))   BEGIN
INSERT INTO single_user (user_name,mobile_no) VALUES (name,mobile);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `newCategory` (IN `name` VARCHAR(30))   BEGIN
INSERT INTO category(cname) values (name);
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `selectbyDate` (IN `dates` DATE, IN `id` INT)   BEGIN
	SELECT * FROM income WHERE date=dates and user_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `selectExpenseByDate` (IN `id` INT(10), IN `dates` DATE)   BEGIN
	SELECT * FROM expense WHERE date=dates and user_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `showCategory` ()   BEGIN
	select * from category;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateAmount` (IN `id` INT(10), IN `amounts` DOUBLE)   BEGIN
UPDATE income set amount=amounts WHERE income_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateDetail` (IN `id` INT(10), IN `details` VARCHAR(30))   BEGIN
UPDATE income set detail=details WHERE income_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateExpenseAmount` (IN `id` INT(10), IN `amounts` DOUBLE)   BEGIN
UPDATE expense SET amount=amounts WHERE expense_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateExpenseCategory` (IN `id` INT(10), IN `c_id` INT(10))   BEGIN
UPDATE expense SET cid=c_id WHERE expense_id=id;
END$$

CREATE DEFINER=`root`@`localhost` PROCEDURE `updateExpenseDetail` (IN `id` INT(10), IN `details` VARCHAR(30))   BEGIN
UPDATE expense SET detail=details WHERE expense_id=id;
END$$

DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

CREATE TABLE `category` (
  `cid` int(11) NOT NULL,
  `cname` varchar(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`cid`, `cname`) VALUES
(1, 'Food'),
(2, 'Transport'),
(3, 'Health'),
(4, 'Education'),
(5, 'Household'),
(6, 'Shopping'),
(7, 'culture');

-- --------------------------------------------------------

--
-- Table structure for table `expense`
--

CREATE TABLE `expense` (
  `expense_id` int(11) NOT NULL,
  `detail` varchar(30) NOT NULL,
  `date` date NOT NULL,
  `amount` double NOT NULL,
  `cid` int(11) NOT NULL,
  `mode` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timeStamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `expense`
--

INSERT INTO `expense` (`expense_id`, `detail`, `date`, `amount`, `cid`, `mode`, `user_id`, `timeStamp`) VALUES
(44, 'Ice-cream', '2024-08-29', 50, 1, 'online', 7, '2024-08-29 13:24:42'),
(46, 'MilkShake', '2024-08-31', 199, 1, 'cash', 1, '2024-08-30 19:02:50'),
(47, 'T-shirt', '2024-08-31', 499, 6, 'online', 1, '2024-08-30 19:04:07');

-- --------------------------------------------------------

--
-- Table structure for table `income`
--

CREATE TABLE `income` (
  `income_id` int(11) NOT NULL,
  `date` date NOT NULL,
  `detail` varchar(30) NOT NULL,
  `amount` double NOT NULL,
  `mode` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `timeStamp` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `income`
--

INSERT INTO `income` (`income_id`, `date`, `detail`, `amount`, `mode`, `user_id`, `timeStamp`) VALUES
(62, '2024-08-16', 'papa', 4000, 'cash', 1, '2024-08-24 02:28:10'),
(63, '2024-06-12', 'mom', 500, 'online', 1, '2024-08-24 02:36:17'),
(65, '2024-08-28', 'efw', 313, 'cash', 6, '2024-08-28 17:54:13'),
(66, '2024-08-29', 'Pocket Money', 15000, 'Cash', 7, '2024-08-29 13:20:17'),
(67, '2024-08-30', 'Friend', 210, 'online', 7, '2024-08-29 13:22:57'),
(70, '2024-08-30', 'Friend', 560, 'cash', 1, '2024-08-30 10:49:27'),
(71, '2024-08-31', 'uncle', 12020, 'online', 1, '2024-08-30 19:01:25'),
(72, '2024-08-31', 'Aunty', 1000, 'online', 1, '2024-08-31 05:31:24'),
(73, '2024-08-31', 'Stock', 9639, 'online', 1, '2024-08-30 19:31:11'),
(74, '2024-08-31', 'jhgjvj', 987654321, 'cash', 1, '2024-08-31 05:50:40');

-- --------------------------------------------------------

--
-- Table structure for table `single_user`
--

CREATE TABLE `single_user` (
  `user_id` int(11) NOT NULL,
  `user_name` varchar(255) NOT NULL,
  `mobile_no` varchar(10) NOT NULL,
  `last_login` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `single_user`
--

INSERT INTO `single_user` (`user_id`, `user_name`, `mobile_no`, `last_login`) VALUES
(1, 'Manthan Gohel', '9106032028', '2024-09-01 08:36:46'),
(2, 'Priya Sharma', '9106032020', '2024-08-23 15:50:21'),
(3, 'Amit Kumar', '7890123456', '2024-08-23 15:50:21'),
(4, 'Sneha Jain', '9870987654', '2024-08-23 15:50:21'),
(5, 'Rohan Gupta', '8976543210', '2024-08-23 15:50:21'),
(6, 'check1', '9513246870', '2024-08-28 17:53:43'),
(7, 'Mithilesh Gupta', '9638527410', '2024-08-29 13:18:53'),
(8, 'Palkesh Trivedi', '9874563210', '2024-08-29 13:29:22');

--
-- Triggers `single_user`
--
DELIMITER $$
CREATE TRIGGER `check_mobile_number` BEFORE UPDATE ON `single_user` FOR EACH ROW IF EXISTS (SELECT 1 FROM single_user WHERE mobile_no = NEW.mobile_no AND user_id != NEW.user_id) THEN
        SIGNAL SQLSTATE '45000' SET MESSAGE_TEXT = 'Duplicate mobile number found';
END IF
$$
DELIMITER ;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`cid`);

--
-- Indexes for table `expense`
--
ALTER TABLE `expense`
  ADD PRIMARY KEY (`expense_id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `foreignkey_category_id` (`cid`);

--
-- Indexes for table `income`
--
ALTER TABLE `income`
  ADD PRIMARY KEY (`income_id`),
  ADD KEY `user_id` (`user_id`);

--
-- Indexes for table `single_user`
--
ALTER TABLE `single_user`
  ADD PRIMARY KEY (`user_id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `category`
--
ALTER TABLE `category`
  MODIFY `cid` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=8;

--
-- AUTO_INCREMENT for table `expense`
--
ALTER TABLE `expense`
  MODIFY `expense_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=48;

--
-- AUTO_INCREMENT for table `income`
--
ALTER TABLE `income`
  MODIFY `income_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=75;

--
-- AUTO_INCREMENT for table `single_user`
--
ALTER TABLE `single_user`
  MODIFY `user_id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `expense`
--
ALTER TABLE `expense`
  ADD CONSTRAINT `expense_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `single_user` (`user_id`),
  ADD CONSTRAINT `foreignkey_category_id` FOREIGN KEY (`cid`) REFERENCES `category` (`cid`);

--
-- Constraints for table `income`
--
ALTER TABLE `income`
  ADD CONSTRAINT `income_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `single_user` (`user_id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
