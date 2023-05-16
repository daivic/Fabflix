USE moviedb;
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
        SELECT 'Failed: Movie Exists in moviedb' AS message;
    ELSE
        SET movieId = CONCAT("tt",(SELECT MAX(SUBSTRING(id, 3)) FROM movies) + 1);
        INSERT INTO movies(id, title, year, director)
        VALUES (movieId, p_movieTitle, p_movieYear, p_movieDirector);
        IF (
            SELECT COUNT(*)
            FROM genres
            WHERE name = p_genreName
        ) = 0 THEN
            SET genreId = (SELECT MAX(id) FROM genres) + 1;
            INSERT INTO genres(id, name) VALUES (genreId, p_genreName);
        END IF;
        INSERT INTO genres_in_movies(genreId, movieId)
        VALUES ((SELECT id FROM genres WHERE name = p_genreName), movieId);
        IF (
            SELECT COUNT(*)
            FROM stars
            WHERE name = p_starName
        ) = 0 THEN
            SET starId = CONCAT("nm",(SELECT MAX(SUBSTRING(id, 3)) FROM stars) + 1);
            INSERT INTO stars(id, name, birthYear) VALUES (starId, p_starName, NULL);
        END IF;
        INSERT INTO stars_in_movies(starId, movieId)
        VALUES ((SELECT id FROM stars WHERE name = p_starName LIMIT 1), movieId);

        SET movieId = (SELECT id FROM movies WHERE title = p_movieTitle AND year = p_movieYear AND director = p_movieDirector LIMIT 1);
        SET starId = (SELECT id FROM stars WHERE name = p_starName LIMIT 1);
        SET genreId = (SELECT id FROM genres WHERE name = p_genreName LIMIT 1);
        
		INSERT INTO ratings(movieId, rating, numVotes) 
		VALUES (movieId, 0, 0);
        
        SELECT CONCAT('Successfully Added: MovieId : ', movieId, ', StarId : ', starId, ', Genre Id : ', genreId) AS message;
    END IF;
END;

DROP PROCEDURE IF EXISTS add_star $$

CREATE PROCEDURE add_star (
	IN p_starName VARCHAR(100),
	IN p_starYear INTEGER
)
BEGIN
	DECLARE starId CHAR(9);
IF (
	(SELECT COUNT(*)
	FROM stars
	WHERE name = p_starName AND birthYear = p_starYear) = 0
) THEN
	-- create star
	SET starId = CONCAT("nm", (SELECT MAX(SUBSTRING(id, 3)) FROM stars) + 1);
	INSERT INTO stars(id, name, birthYear) VALUES (starId, p_starName, p_starYear);
	SELECT CONCAT('Successfully added: StarID:', starId) AS message;

ELSE
	SELECT 'Failed: Star Exists' AS message;

END IF;
END$$
DELIMITER ;
