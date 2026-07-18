-- ============================================================
-- V1: Initial Menu Service Schema
-- ============================================================

-- 1. RESTAURANTS
CREATE TABLE restaurants (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(255)        NOT NULL,
    description     TEXT,
    cuisine_tags    VARCHAR(100)[]      NOT NULL DEFAULT '{}',
    is_open         BOOLEAN             NOT NULL DEFAULT true,
    latitude        NUMERIC(9, 6)       NOT NULL,
    longitude       NUMERIC(9, 6)       NOT NULL,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ
);

-- 2. MENU CATEGORIES
CREATE TABLE menu_categories (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    restaurant_id   UUID                NOT NULL REFERENCES restaurants(id),
    name            VARCHAR(255)        NOT NULL,
    sort_order      INT                 NOT NULL DEFAULT 0,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ
);

-- 3. MENU ITEMS
CREATE TABLE menu_items (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    category_id         UUID                NOT NULL REFERENCES menu_categories(id),
    name                VARCHAR(255)        NOT NULL,
    description         TEXT,
    base_price_cents    INT                 NOT NULL CHECK (base_price_cents >= 0),
    image_url           VARCHAR(512),
    is_available        BOOLEAN             NOT NULL DEFAULT true,
    is_vegan            BOOLEAN             NOT NULL DEFAULT false,
    is_gluten_free      BOOLEAN             NOT NULL DEFAULT false,
    created_at          TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at          TIMESTAMPTZ
);

-- 4. OPTION GROUPS
CREATE TABLE option_groups (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    menu_item_id    UUID                NOT NULL REFERENCES menu_items(id),
    name            VARCHAR(255)        NOT NULL,
    min_selectable  INT                 NOT NULL DEFAULT 0 CHECK (min_selectable >= 0),
    max_selectable  INT                 NOT NULL DEFAULT 1 CHECK (max_selectable >= 1),
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ,
    CONSTRAINT chk_selectable CHECK (max_selectable >= min_selectable)
);

-- 5. ITEM OPTIONS
CREATE TABLE item_options (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    option_group_id UUID                NOT NULL REFERENCES option_groups(id),
    name            VARCHAR(255)        NOT NULL,
    price_cents     INT                 NOT NULL DEFAULT 0 CHECK (price_cents >= 0),
    is_available    BOOLEAN             NOT NULL DEFAULT true,
    created_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMPTZ         NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at      TIMESTAMPTZ
);

-- ============================================================
-- INDEXES (avoid full table scans on API read hotpaths)
-- ============================================================

-- FK indexes for JOIN performance
CREATE INDEX idx_menu_categories_restaurant_id  ON menu_categories(restaurant_id);
CREATE INDEX idx_menu_items_category_id         ON menu_items(category_id);
CREATE INDEX idx_option_groups_menu_item_id     ON option_groups(menu_item_id);
CREATE INDEX idx_item_options_option_group_id   ON item_options(option_group_id);

-- Partial indexes on FK columns: optimise the common query pattern
-- "give me all active children of parent X" without scanning soft-deleted rows
CREATE INDEX idx_menu_categories_active         ON menu_categories(restaurant_id) WHERE deleted_at IS NULL;
CREATE INDEX idx_menu_items_active              ON menu_items(category_id)        WHERE deleted_at IS NULL;
CREATE INDEX idx_option_groups_active           ON option_groups(menu_item_id)    WHERE deleted_at IS NULL;
CREATE INDEX idx_item_options_active            ON item_options(option_group_id)  WHERE deleted_at IS NULL;

-- ============================================================
-- TRIGGERS: auto-update updated_at on every row update
-- ============================================================
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_restaurants_updated_at
    BEFORE UPDATE ON restaurants
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_menu_categories_updated_at
    BEFORE UPDATE ON menu_categories
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_menu_items_updated_at
    BEFORE UPDATE ON menu_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_option_groups_updated_at
    BEFORE UPDATE ON option_groups
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

CREATE TRIGGER trg_item_options_updated_at
    BEFORE UPDATE ON item_options
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();
