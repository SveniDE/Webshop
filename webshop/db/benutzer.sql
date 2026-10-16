CREATE USER IF NOT EXISTS 'shopuser'@'localhost' IDENTIFIED BY 'shoppass';
GRANT ALL PRIVILEGES ON shop.* TO 'shopuser'@'localhost';
FLUSH PRIVILEGES;
