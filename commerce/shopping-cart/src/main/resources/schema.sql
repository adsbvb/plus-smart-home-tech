CREATE TABLE IF NOT EXISTS carts (
    cart_id UUID DEFAULT get_random_uuid() PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    is_active BOOLEAN DEAFULT TRUE
);

CREATE TABLE IF NOT EXISTS cart_products (
    id UUID DEFAULT get_random_uuid() PRIMARY KEY,
    cart_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (cart_id) REFERENCES carts(cart_id) ON DELETE CASCADE,
    CONSTRAINT unique_cart_product UNIQUE (cart_id, product_id)
);