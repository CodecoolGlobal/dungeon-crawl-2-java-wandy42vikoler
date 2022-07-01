DROP TABLE IF EXISTS playerSaveSlot;

CREATE TABLE playerSaveSlot (
                        id SERIAL PRIMARY KEY,
                        player_name VARCHAR(20) NOT NULL,
                        player_level VARCHAR(20) NOT NULL
);