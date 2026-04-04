CREATE TABLE IF NOT EXISTS orders (
    order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    order_state VARCHAR(20) NOT NULL DEFAULT 'NEW',
    shopping_cart_id UUID,
    delivery_id UUID,
    payment_id UUID,
    delivery_volume DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (delivery_volume >= 0),
    delivery_weight DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (delivery_weight >= 0),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    delivery_price DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (delivery_price >= 0),
    product_price DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (product_price >= 0),
    total_price DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (total_price >= 0)
);

CREATE TABLE IF NOT EXISTS order_products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT unique_order_product UNIQUE (order_id, product_id)
);