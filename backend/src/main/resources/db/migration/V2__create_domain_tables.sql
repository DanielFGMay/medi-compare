-- Migración V2: Tablas de dominio para comparador de farmacias
-- Chain: red de farmacias
-- Branch: sucursales
-- Product: productos
-- Offer: ofertas de precios

-- Tabla de cadenas de farmacias
CREATE TABLE IF NOT EXISTS chain (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    logo_url TEXT,
    website TEXT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de sucursales
CREATE TABLE IF NOT EXISTS branch (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    chain_id UUID NOT NULL REFERENCES chain(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    address TEXT,
    latitude DECIMAL(10, 8),
    longitude DECIMAL(11, 8),
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índice para búsquedas geográficas
CREATE INDEX IF NOT EXISTS idx_branch_location ON branch(latitude, longitude);

-- Tabla de categorías de productos
CREATE TABLE IF NOT EXISTS category (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    code VARCHAR(50) UNIQUE,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS product (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(500) NOT NULL,
    category_id UUID REFERENCES category(id),
    is_rx BOOLEAN DEFAULT FALSE,
    strength VARCHAR(100),
    form VARCHAR(100),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Tabla de ofertas de precios
CREATE TABLE IF NOT EXISTS offer (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    product_id UUID NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    branch_id UUID REFERENCES branch(id) ON DELETE SET NULL,
    chain_id UUID NOT NULL REFERENCES chain(id) ON DELETE CASCADE,
    price DECIMAL(10, 2) NOT NULL,
    price_promo DECIMAL(10, 2),
    promo_text TEXT,
    product_url TEXT,
    scraped_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- Índices para consultas frecuentes
CREATE INDEX IF NOT EXISTS idx_offer_product ON offer(product_id);
CREATE INDEX IF NOT EXISTS idx_offer_chain ON offer(chain_id);
CREATE INDEX IF NOT EXISTS idx_offer_branch ON offer(branch_id);
CREATE INDEX IF NOT EXISTS idx_offer_scraped_at ON offer(scraped_at);
CREATE INDEX IF NOT EXISTS idx_product_name ON product(name);

-- Comentario explicativo
COMMENT ON TABLE chain IS 'Red de farmacias que participan en el comparador';
COMMENT ON TABLE branch IS 'Sucursal de una cadena de farmacias';
COMMENT ON TABLE product IS 'Producto farmacéutico a comparar';
COMMENT ON TABLE offer IS 'Oferta de precio de un producto en una sucursal';