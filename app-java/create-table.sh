docker exec mysql_master sh -c "export MYSQL_PWD=111; mysql -u root mydb -e 'CREATE TABLE tbl_test (id INT PRIMARY KEY AUTO_INCREMENT, data INT);'"
