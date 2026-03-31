CREATE DATABASE shopping_store;
CREATE DATABASE shopping_cart;
CREATE DATABASE warehouse;
CREATE DATABASE shopping_order;

GRANT ALL PRIVILEGES ON DATABASE shopping_store TO commerce_user;
GRANT ALL PRIVILEGES ON DATABASE shopping_cart TO commerce_user;
GRANT ALL PRIVILEGES ON DATABASE warehouse TO commerce_user;
GRANT ALL PRIVILEGES ON DATABASE shopping_order TO commerce_user;