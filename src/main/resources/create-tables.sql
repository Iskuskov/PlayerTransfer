-- 0. Drop tables if exist
DROP TABLE IF EXISTS players;
DROP TABLE IF EXISTS teams;


-- 1. Teams
CREATE TABLE teams (
    team_id SERIAL PRIMARY KEY,
    team_name VARCHAR (128),
    country VARCHAR (128),
    budget INTEGER
);


-- 2. Players
CREATE TABLE players (
    player_id SERIAL PRIMARY KEY,
    player_name VARCHAR (128),
    nationality VARCHAR (128),
    market_value INTEGER,
    team_id INTEGER --REFERENCES teams (team_id)
);


