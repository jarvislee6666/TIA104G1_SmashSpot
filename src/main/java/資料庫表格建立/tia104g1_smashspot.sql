CREATE DATABASE tia104g1_smashspot;
USE tia104g1_smashspot;

CREATE TABLE coupon(
    cop_id Int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    cop_code VARCHAR(20) NOT NULL,
    crt_date Date DEFAULT (CURRENT_DATE) NOT NULL ,	
    end_date Date NOT NULL,
    discount Int NOT NULL
);

INSERT INTO coupon(cop_code,end_date,discount)VALUES
('USED2HAND2024', '2024-12-31', 200),
('FIRSTBUY50', '2024-12-31', 100),
('SUMMER2ND', '2024-12-31', 100),
('OLDNEW20', '2024-12-31', 200),
('RACKET10OFF', '2024-12-31', 100);




CREATE TABLE admin(
    adm_id Int PRIMARY KEY AUTO_INCREMENT NOT NULL,
    adm_email VARCHAR(50) NOT NULL UNIQUE KEY,
    adm_password VARCHAR(20) NOT NULL UNIQUE KEY,
    adm_name VARCHAR(20) NOT NULL,
    adm_phone VARCHAR(20) NOT NULL UNIQUE KEY,
    hr_date Date NOT NULL,
    upd_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP NOT NULL,
    adm_sta BOOLEAN NOT NULL,
    adm_bday DATE NOT NULL,
    supvsr BOOLEAN NOT NULL
);

INSERT INTO admin(adm_email,adm_password,adm_name,adm_phone,hr_date,adm_sta,adm_bday,supvsr)VALUES
('admin123@gmail.com','admin123','Admin01','0987878787','2024-12-02',true,'1998-12-31',true),
('smashspot123@gmail.com','admin456','Kevin','0966666666','2024-12-02',true,'1999-12-31',false);




CREATE TABLE member (
    mem_id INT(11) NOT NULL AUTO_INCREMENT PRIMARY KEY,
    account VARCHAR(100) NOT NULL UNIQUE KEY,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL  UNIQUE KEY,
    crt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    chg_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    phone VARCHAR(15) NOT NULL  UNIQUE KEY,
    bday DATE NOT NULL,
    addr VARCHAR(100) NOT NULL,
    status BOOLEAN NOT NULL,
    mem_pic MEDIUMBLOB
   );
  
  
  INSERT INTO `member` (account,password,name,email,phone,bday,addr,status) VALUES 
 ('test123','password123','王小明','test123@example.com','0912345678','1990-01-01','台北市信義區信義路100號',TRUE),
 ('test456','password456','張小華','test456@example.com','0987654321','1996-01-01','台北市中山區中山北路100號',TRUE),
 ('test789','password789','林小美','test789@example.com','0922222222','1998-01-01','台北市信義區松壽路100號',TRUE);




--	0117念芸: 調整INSERT假資料內容
-- 創建地點表格
CREATE TABLE location (
    loc_id INT PRIMARY KEY AUTO_INCREMENT,
    region VARCHAR(20) NOT NULL
);

-- 插入假資料
INSERT INTO location (
    region
) VALUES 
('台北市'),
('新北市'),
('桃園市'),
('台中市'),
('台南市'),
('高雄市'),
('基隆市'),
('新竹市'),
('嘉義市'),
('宜蘭縣'),
('新竹縣'),
('苗栗縣'),
('彰化縣'),
('南投縣'),
('雲林縣'),
('嘉義縣'),
('屏東縣'),
('台東縣'),
('花蓮縣'),
('澎湖縣'),
('金門縣'),
('連江縣');


--	1225念芸: 修改open_time、close_time欄位順序、調整INSERT指令內容
--	0117念芸: 調整INSERT假資料內容

CREATE TABLE stadium (
	stdm_id	     	  INT AUTO_INCREMENT NOT NULL,
	stdm_name	  	  VARCHAR(20) NOT NULL,
	stdm_addr	  	  VARCHAR(50) NOT NULL,
	loc_id            INT NOT NULL,
	longitude         DECIMAL(11, 8) NOT NULL,
	latitude          DECIMAL(10, 8) NOT NULL,
	stdm_intro  	  TEXT NOT NULL,
	court_count       INT NOT NULL,
	court_price       INT NOT NULL,
	opr_sta  		  BOOLEAN NOT NULL,
	stdm_pic   		  MEDIUMBLOB,
	adm_id            INT NOT NULL,
	opentime         INT NOT NULL,
	closetime        INT NOT NULL,
	stdm_start_time   TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,


	
	CONSTRAINT fk_location_stadium_loc_id	
         FOREIGN KEY (loc_id) REFERENCES location (loc_id),
     CONSTRAINT fk_admin_stadium_admin_id
         FOREIGN KEY (adm_id) REFERENCES admin (adm_id),
	CONSTRAINT stadium_PRIMARY_KEY 
	PRIMARY KEY (stdm_id)
) AUTO_INCREMENT = 1;

INSERT INTO stadium (stdm_name, stdm_addr, loc_id, longitude, latitude, stdm_intro, court_count, court_price, opr_sta, stdm_pic, adm_id, opentime, closetime)
VALUES
('飆汗羽球_信義總部', '台北市信義區松勤街100號', 1, 121.56677100, 25.03169960, '北市最牛羽球館，給你最極致的羽球饗宴', 5, 600, 1, NULL, 1, 8, 22),
('飆汗羽球_大甲格鬥館', '台中市大甲區中山路一段876號', 4, 120.62781130, 24.34790770, '呼風喚羽大甲格鬥館', 5, 450, 1, NULL, 2, 8, 22),
('飆汗羽球_東泰分館', '桃園市八德區東泰街201號', 3, 121.27476870, 24.96422130, '桃園最大羽球館(目前閉館維護中)', 1, 250, 0, NULL, 2, 8, 22),
('新店國民運動中心', '新北市新店區北新路一段88巷12號', 2, 121.54247220, 24.96717740, '本場地以從事羽球運動為主，恕不提供其他運動或活動使用，如有球具租借需求，請至3F櫃台辦理', 6, 400, 0, NULL, 1, 8, 22),
('南港運動中心', '台北市南港區玉成街69號', 1, 121.58188260, 25.04888140, '(春節場地整修中)南港運動中心是台北市第4座啟用市民運動中心，其樓地板面積17,540平方公尺，建物為地上8層、地下4層', 4, 500, 0, NULL, 1, 8, 22),
('國立中興大學體育館', '台中市南區興大路145號', 4, 120.67624260, 24.11844520, '羽球場為本館最標準之運動場館，提供羽球、手球教學及訓練使用，共有5面符合國際標準規格之羽球比賽場地', 5, 550, 1, NULL, 2, 8, 22),
('國立政治大學體育館', '台北市文山區指南路二段64號', 1, 121.57592040, 24.98741710, '六座羽球場，場地寬廣、挑高搭配專業防滑的PU地板，智能空調系統及大片式採光，讓揮拍時更舒爽、順暢', 6, 800, 1, NULL, 1, 8, 22),
('飆汗羽球_三民分館', '高雄市三民區九如一路720號', 6, 120.32255120, 22.64143060, '歡迎光臨飆汗羽球_三民分館', 4, 450, 1, NULL, 2, 8, 22),
('飆汗羽球_永康分館', '台南市永康區中正南路592巷68號', 5, 120.23554120, 23.03622570, '歡迎光臨飆汗羽球_永康分館', 4, 450, 1, NULL, 2, 8, 22);


-- 創建場館收藏清單表格
CREATE TABLE stadium_like (
    stdm_like_id INT PRIMARY KEY AUTO_INCREMENT,
    mem_id INT NOT NULL,
    stdm_id INT NOT NULL,
     FOREIGN KEY (mem_id) REFERENCES member(mem_id),
     FOREIGN KEY (stdm_id) REFERENCES stadium(stdm_id)
);


INSERT INTO stadium_like (
    mem_id,
    stdm_id
) VALUES 
(1, 1),
(1, 2),
(1, 3),
(2, 1);


-- 創建預約時段總覽表格
CREATE TABLE reservation_time (
    rsv_time_id INT PRIMARY KEY AUTO_INCREMENT,
    stdm_id INT NOT NULL,
    dates DATE NOT NULL,
    booked VARCHAR(12) NOT NULL,
    rsv_ava VARCHAR(12) NOT NULL,
       FOREIGN KEY (stdm_id) REFERENCES stadium(stdm_id)
);

INSERT INTO reservation_time (
    stdm_id,
    dates,
    booked,
    rsv_ava
) VALUES 
(1, '2024-03-15', 'xxxx565553xx', 'xxxx777777xx'),
(1, '2024-03-16', 'xxxx555555xx', 'xxxx777777xx'),
(1, '2024-03-17', 'xxxx033300xx', 'xxxx777777xx'),
(2, '2024-03-15', 'xxxx3242000x', 'xxxx5555555x'),
(2, '2024-03-16', 'xxxx330000x', 'xxxx5555555x'),
(2, '2024-03-17', 'xxxx0000000x', 'xxxx5555555x');



-- 創建場地訂單表格
CREATE TABLE court_order (
    court_ord_id INT PRIMARY KEY AUTO_INCREMENT,
    mem_id INT NOT NULL,
    stdm_id INT NOT NULL,
    rsv_sta BOOLEAN NOT NULL,
    crt_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tot_amt INT NOT NULL,
    can_reason TEXT,
    star_rank INT ,
    message TEXT,
     FOREIGN KEY (mem_id) REFERENCES member(mem_id),
     FOREIGN KEY (stdm_id) REFERENCES stadium(stdm_id)
);

-- 插入假資料
INSERT INTO court_order (
    mem_id,
    stdm_id,
    rsv_sta,
    tot_amt,
    can_reason,
	star_rank,
    message
) VALUES 
(1, 1, TRUE, 1200, NULL, 5, '場地很乾淨，設備新穎，非常滿意！'),
(1, 2, FALSE, 1350, '臨時有事無法前往', NULL, NULL),
(2, 1, TRUE, 2400, NULL, 4, '服務人員態度良好，環境整潔');



-- 創建場地訂單明細表格
CREATE TABLE court_order_detail (
    ord_dtl_id INT PRIMARY KEY AUTO_INCREMENT,
    court_ord_id INT NOT NULL,
    ord_date DATE NOT NULL,
    ord_time VARCHAR(12) NOT NULL,
    FOREIGN KEY (court_ord_id) REFERENCES court_order(court_ord_id)
);

-- 插入假資料
INSERT INTO court_order_detail (
    court_ord_id,
    ord_date,
    ord_time
) VALUES 
(1, '2024-03-15', 'xxx00011000x'),
(2, '2024-03-16', 'xxx00110000x'),
(2, '2024-03-17', 'xxx00000100x'),
(3, '2024-03-18', 'xxx00011100x'),
(3, '2024-03-19', 'xxx00100000x');


CREATE TABLE product_classification (
	pro_class_id	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	cate_name	varchar(20) not null
);

INSERT INTO product_classification (cate_name) VALUES 
('羽毛球拍'),
('羽毛球'),
('羽毛球鞋'),
('羽毛球服裝'),
('羽毛球配件');

CREATE TABLE product (
	pro_id	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	mem_id	INT not null,
	pro_class_id INT NOT NULL,
    bid_sta_id	int NOT NULL,
    base_price int NOT NULL,
    pur_price int NOT NULL,
    intro Varchar(255) NOT NULL,
    pro_start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, 
    end_time TIMESTAMP NOT NULL ,
    mod_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    min_incr int NOT NULL,
    pro_name Varchar(30) NOT NULL,
    pro_pic MEDIUMBLOB,
    max_price int,
        FOREIGN KEY (pro_class_id) REFERENCES product_classification (pro_class_id),
        FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);

INSERT INTO product (
   mem_id, pro_class_id, bid_sta_id, 
   base_price, pur_price, intro, 
   end_time, min_incr, 
   pro_name, max_price
) VALUES 
(3, 1, 1, 
500, 1500, '9成新YONEX羽毛球拍，性能優異，適合業餘選手', 
'2024-07-15 20:00:00', 100, 
'YONEX球拍', 1200),

(3, 2, 2, 
200, 700, '全新professional級別羽毛球，比賽專用', 
'2024-07-20 21:00:00', 100, 
'比賽級羽毛球', 500),

(3, 3, 3, 
800, 2000, '李寧專業羽毛球鞋，尺寸US27，輕量透氣', 
'2024-07-25 19:30:00', 200, 
'李寧羽球鞋',NULL),

(3, 3, 2, 
800, 2000, 'YY專業羽毛球鞋，尺寸US27，輕量透氣', 
'2024-07-25 19:30:00', 100, 
'YY專業羽毛球鞋', 1500),

(3, 1, 2, 
800, 2000, '勝利專業羽毛球拍，性能優異，適合業餘選手', 
'2024-07-25 19:30:00', 100, 
'勝利專業羽毛球拍', 1500);


CREATE TABLE favorites_list (
	favor_list_id	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	mem_id	INT not null,
	pro_id	INT NOT NULL,
        FOREIGN KEY (pro_id) REFERENCES product (pro_id),
        FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);

INSERT INTO favorites_list (mem_id, pro_id) VALUES 
(1, 1),
(1, 2),
(1, 3),
(2, 1);


CREATE TABLE bid (
	bid_id	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
	mem_id	int not null,  
	pro_id	int NOT NULL,
    bid_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL, 
    bid_amt int NOT NULL,
    CONSTRAINT fk_bid_product_product_id
        FOREIGN KEY (pro_id) REFERENCES product (pro_id),
        FOREIGN KEY (mem_id) REFERENCES member(mem_id)
);

INSERT INTO bid (mem_id, pro_id, bid_amt) VALUES 
(1, 1, 600),
(1, 1, 700),
(1, 2, 500),
(2, 1, 1200),
(2, 4, 1000),
(2, 5, 1000 ),
(1, 4, 1500),
(1, 5, 1500)
;


CREATE TABLE orders(
	ord_id	INT AUTO_INCREMENT NOT NULL PRIMARY KEY,
    ord_sta_id INT NOT NULL,
    pro_ord_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    before_dis INT NOT NULL,
    after_dis INT NOT NULL,
    send_info varchar(255) not null,
    cop_id INT,
    mem_id	INT not null,
    pro_id	INT NOT NULL,
        FOREIGN KEY (pro_id) REFERENCES product (pro_id),
        FOREIGN KEY (cop_id) REFERENCES coupon (cop_id)
);

INSERT INTO orders (
   ord_sta_id, 
   before_dis, 
   after_dis, 
   send_info, 
   cop_id, 
   mem_id, 
   pro_id
) VALUES 
(1, 500, 400, '台北市信義區信義路100號', 2, 1, 2),
(2, 2000, 1900, '台北市信義區信義路100號', 3, 1, 4),
(4, 800, 600, '台中市西屯區逢甲路101號', 4, 1, 5);




	
