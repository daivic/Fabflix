DROP procedure IF EXISTS add_movie;
DELIMITER $$
USE moviedb $$

CREATE PROCEDURE add_movie (
    IN p_movieTitle VARCHAR(100),
    IN p_movieDirector VARCHAR(100),
    IN p_genreName VARCHAR(32),
    IN p_starName VARCHAR(100),
    IN p_movieYear INTEGER
)
BEGIN
    DECLARE movieId CHAR(9);
    DECLARE genreId INTEGER;
    DECLARE starId CHAR(9);

    IF (
        SELECT COUNT(*)
        FROM movies
        WHERE title = p_movieTitle
        AND year = p_movieYear
        AND director = p_movieDirector
    ) > 0 THEN
        SELECT 'FAILURE: Movie already exists. No changes have been made.' AS message;
    ELSE
        -- generate movie id
        SET movieId = CONCAT("tt",(SELECT MAX(SUBSTRING(id, 3)) FROM movies) + 1);
        
        
        
        INSERT INTO movies(id, title, year, director)
        VALUES (movieId, p_movieTitle, p_movieYear, p_movieDirector);

        -- genre
        IF (
            SELECT COUNT(*)
            FROM genres
            WHERE name = p_genreName
        ) = 0 THEN
            -- create genre
            SET genreId = (SELECT MAX(id) FROM genres) + 1;
            INSERT INTO genres(id, name) VALUES (genreId, p_genreName);
        END IF;

        -- link genre to movie
        INSERT INTO genres_in_movies(genreId, movieId)
        VALUES ((SELECT id FROM genres WHERE name = p_genreName), movieId);

        -- star
        IF (
            SELECT COUNT(*)
            FROM stars
            WHERE name = p_starName
        ) = 0 THEN
            -- create star
            SET starId = CONCAT("nm",(SELECT MAX(SUBSTRING(id, 3)) FROM stars) + 1);
            INSERT INTO stars(id, name, birthYear) VALUES (starId, p_starName, NULL);
        END IF;

        -- link star to movie
        INSERT INTO stars_in_movies(starId, movieId)
        VALUES ((SELECT id FROM stars WHERE name = p_starName LIMIT 1), movieId);

        SET movieId = (SELECT id FROM movies WHERE title = p_movieTitle AND year = p_movieYear AND director = p_movieDirector LIMIT 1);
        SET starId = (SELECT id FROM stars WHERE name = p_starName LIMIT 1);
        SET genreId = (SELECT id FROM genres WHERE name = p_genreName LIMIT 1);
        
		INSERT INTO ratings(movieId, rating, numVotes) 
		VALUES (movieId, 0, 0);
        
        SELECT CONCAT('SUCCESS: MovieId : ', movieId, ', StarId : ', starId, ', Genre Id : ', genreId) AS message;
    END IF;
END;
