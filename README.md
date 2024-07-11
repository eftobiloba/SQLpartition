# SQLpartition
A research and development project to check the efficiency of using partitions to query data in a very large table with a lot of rows.

## Is this project worth it?
If you want to play with the possibility of using partitions to improve performance and reduce response time for queries when you have a very large amount of rows in the table, yes. It is. I have written the endpoints so you can just clone and test. The mysql and postgresql DDL scripts are also attached to this readme.

## More info
If you decide to use this repo for anything, a few things to note:
1. The server when running will automatically get random user data from `randomuser.me/api` every 30 seconds. This is to populate the 'user' table.
2. Depending on which database you use (mysql/postgreSql), make sure you have the sql server running (you can edit the application.properties) to fit your configurations.
3. MySQL is limited in creating partitions before an insert. If you're using mysql, I have a `partition service` in the `services` folder that you can call to automatically do the partition check and add. You can add it before you insert to the table from the server.

## Java code explaination
In the spring boot code files above, I have 5 endpoints:
1. JPA `insert` endpoint
2. JPA `select user_id` endpoint
3. JDBC `insert` endpoint
4. JDBC `select user_id` endpoint
5. Endpoint to view automatic data addition (open in browser)

## MYSQL DDL Script
```
USE user_db;

CREATE TABLE user_partitioned_new (
    `user_id` VARCHAR(17),
    `name` VARCHAR(30),
    `address` VARCHAR(255),
    `dob` DATE,
    `email` VARCHAR(50),
    `phone` VARCHAR(20),
    `date` DATE,
    PRIMARY KEY (`user_id`, `date`)
)
PARTITION BY RANGE COLUMNS(`date`) (
    PARTITION p_initial VALUES LESS THAN ('2024-01-01'),
    PARTITION pMAX VALUES LESS THAN (MAXVALUE)
);

DELIMITER $$

CREATE TRIGGER before_insert_user_partitioned_new
BEFORE INSERT ON user_partitioned_new
FOR EACH ROW
BEGIN
    SET NEW.date = STR_TO_DATE(SUBSTRING(NEW.user_id, 1, 8), '%Y%m%d');
END $$

DELIMITER ;

-- space space space space space space space space space space space --

DELIMITER //
 
CREATE PROCEDURE AddPartition(partition_date DATE)
BEGIN
    DECLARE partition_name VARCHAR(255);
    DECLARE partition_value VARCHAR(255);
    SET partition_name = DATE_FORMAT(partition_date, 'p%Y%m%d');
    SET partition_value = DATE_FORMAT(partition_date + INTERVAL 1 DAY, '%Y-%m-%d');
    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.partitions
        WHERE table_schema = DATABASE()
          AND table_name = 'user'
          AND partition_name = partition_name
    ) THEN
        SET @sql = CONCAT(
            'ALTER TABLE `user` ',
            'ADD PARTITION (',
                'PARTITION ', partition_name, ' ',
                'VALUES LESS THAN (\'', partition_value, '\')'
            ')'
        );
        PREPARE stmt FROM @sql;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END;
 
DELIMITER ;


-- space space space space space space space space space space space space --

DELIMITER $$

CREATE EVENT add_partition_event
ON SCHEDULE EVERY 1 DAY
DO
CALL AddPartition(CURDATE() + INTERVAL 2 DAY) $$

DELIMITER ;

-- space space space space space space space space space space space space --

DELIMITER $$

CREATE TRIGGER before_insert_user_partitioned
BEFORE INSERT ON user_partitioned_new
FOR EACH ROW
BEGIN
    DECLARE partition_date DATE;
    SET partition_date = STR_TO_DATE(SUBSTRING(NEW.user_id, 1, 8), '%Y%m%d');
    CALL AddPartition(partition_date);
END $$

DELIMITER ;

RENAME TABLE user_partitioned_new TO `user`;
```

## PostgreSQL DDL Script
```
CREATE TABLE user_partitioned (
    user_id VARCHAR(17) PRIMARY KEY,
    name VARCHAR(30),
    address VARCHAR(255),
    dob TIMESTAMP,
    email VARCHAR(50),
    phone VARCHAR(20),
    date DATE
) PARTITION BY RANGE (date);

CREATE TABLE user_partitioned_202401 PARTITION OF user_partitioned
    FOR VALUES FROM ('2024-01-01') TO ('2024-02-01');

CREATE TABLE user_partitioned_202402 PARTITION OF user_partitioned
    FOR VALUES FROM ('2024-02-01') TO ('2024-03-01');

CREATE OR REPLACE FUNCTION add_partition(partition_date DATE) RETURNS VOID AS $$
DECLARE
    partition_name TEXT;
    partition_start DATE;
    partition_end DATE;
BEGIN
    partition_start := DATE_TRUNC('month', partition_date);
    partition_end := partition_start + INTERVAL '1 month';
    partition_name := 'user_partitioned_' || TO_CHAR(partition_start, 'YYYYMM');
    
    EXECUTE format('
        CREATE TABLE IF NOT EXISTS %I PARTITION OF user_partitioned
        FOR VALUES FROM (%L) TO (%L)',
        partition_name,
        partition_start,
        partition_end
    );
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION before_insert_user_partitioned()
RETURNS TRIGGER AS $$
BEGIN
    PERFORM add_partition(NEW.date);
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER before_insert_trigger
BEFORE INSERT ON user_partitioned
FOR EACH ROW EXECUTE FUNCTION before_insert_user_partitioned();

INSERT INTO user_partitioned (user_id, name, address, dob, email, phone, date)
VALUES ('20240627052323456', 'Johnson Duns', '123 Jackson Ville', '1990-03-28 00:00:00', 'johnsondns@example.com', '321-654-0987', '2024-06-27');

SELECT
    nmsp_child.nspname AS child_schema,
    child.relname AS child_table
FROM pg_inherits
JOIN pg_class parent ON pg_inherits.inhparent = parent.oid
JOIN pg_class child ON pg_inherits.inhrelid = child.oid
JOIN pg_namespace nmsp_parent ON nmsp_parent.oid = parent.relnamespace
JOIN pg_namespace nmsp_child ON nmsp_child.oid = child.relnamespace
WHERE parent.relname = 'user_partitioned';
```
