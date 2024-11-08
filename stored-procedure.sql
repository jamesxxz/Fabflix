-- Change DELIMITER to $$ 
DELIMITER $$ 

CREATE PROCEDURE add_movie (
    IN movie_title VARCHAR(100),
    IN movie_year INT,
    IN movie_director VARCHAR(100),
    IN star VARCHAR(100),
    IN genre VARCHAR(32),
    OUT statusResp VARCHAR(100)
)
BEGIN
    DECLARE movieId VARCHAR(10);
    DECLARE genreId INT;
    DECLARE starId VARCHAR(10);

    -- check if movie exists
    SELECT id into movieId
    FROM movies
    WHERE title = movie_title AND year = movie_year AND director = movie_director;  -- identify by 3 fields

    IF movieId is NOT NULL THEN 
        SET statusResp = "Error: Movie already exists, please try other input.";

    ELSE
        -- generate new movie id
        SELECT CONCAT("tt0", SUBSTRING(max(id), 4) + 1) INTO movieId
        FROM movies;

        -- insert new movie into table
        INSERT INTO movies (id, title, year, director)
        VALUES (movieId, movie_title, movie_year, movie_director);

        -- check if star exists
        SELECT id INTO starId
        FROM stars
        WHERE name = star;


        -- handle star id
        IF starId IS NULL THEN
            SELECT CONCAT("nm", SUBSTRING(max(id), 3) + 1) INTO starId
            FROM stars;

            -- create new star
            INSERT INTO stars (id, name, birthYear)
            VALUES (starId, star, null);
        END IF;

        INSERT INTO stars_in_movies (starId, movieId)
        VALUES (starId, movieId);

        -- handle genre id
        IF genreId IS NULL THEN
            SELECT max(id) + 1 INTO genreId
            FROM genres;

            -- create new genre
            INSERT INTO stars (id, name)
            VALUES (id, genre);
        END IF;

        INSERT INTO genres_in_movies (genreId, movieId)
        VALUES (genreId, movieId);

        SET statusResp = "Successfully inserted new movie: " || movie_title || "!" || "Movie ID: " || movieId || "Star ID: " || starId || "Genre ID: " || genreId;
    END IF;
END $$;

-- Change back DELIMITER to ; 
DELIMITER ; 
