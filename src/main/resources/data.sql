-- For testing purposes, we need to create initial users to enter the system.
-- The password for both users is simply "password" (without the double quotes).
INSERT INTO _user(id, username, email, password, role, active) VALUES
('8105f2a7-eb8c-4189-9d17-43f160144881', 'John Doe', 'john@bookstore.com', '$2a$10$Uv7sYfiloFleUGz/NJSAAu6MhX6jYbN5pY2wqz3ilEpQ2my1s9SEq', 1, true),
('00cf7ba5-476c-4d0b-9dda-40e545f217e2', 'Robb Doe', 'robb@bookstore.com', '$2a$10$Uv7sYfiloFleUGz/NJSAAu6MhX6jYbN5pY2wqz3ilEpQ2my1s9SEq', 0, true),
('71abf206-f3b9-4a29-971d-b078db51bc00', 'POS #001', NULL,                 '$2a$10$Uv7sYfiloFleUGz/NJSAAu6MhX6jYbN5pY2wqz3ilEpQ2my1s9SEq', 0, true);

INSERT INTO publisher(name) VALUES
('Penguin Random House'),
('HarperCollins'),
('Tor Books'),
('DAW Books'),
('Megadodo Publications'),
('Bantam Spectra'),
('Voyager Books'),
('Allen & Unwin'),
('SuperNowa'),
('O''Reilly Media'),
('Secker & Warburg'),
('Arkham House'),
('Simon & Schuster');

INSERT INTO author(name) VALUES
('Robert Jordan'),
('George R. R. Martin'),
('Brandon Sanderson'),
('Douglas Adams'),
('John R. R. Tolkien'),
('Andrzej Sapkowski'),
('George Orwell'),
('Tad Williams'),
('H. P. Lovecraft'),
('Stephen King');

INSERT INTO book(isbn, title, base_price, publisher_id, quantity) VALUES
('0-312-85009-3',     'The Eye of the World',   7.49, 3, 0),
('0-312-85140-5',     'The Great Hunt',         9.49, 3, 0),
('0-312-85248-7',     'The Dragon Reborn',      7.49, 3, 0),
('0-312-85431-5',     'The Shadows Rising',     9.99, 3, 0),
('0-312-85427-7',     'The Fires of Heaven',    9.99, 3, 0),
('0-312-85428-5',     'The Lord of Chaos',      9.99, 3, 0),
('0-312-85767-5',     'A Crown of Swords',      9.99, 3, 0),
('0-312-85769-1',     'The Path of Daggers',    9.99, 3, 0),
('0-312-86425-6',     'Winter''s Heart',        9.99, 3, 0),
('0-312-86459-0',     'Crossroads of Twilight', 9.99, 3, 0),
('0-7653-0629-8',     'The New Spring',         9.99, 3, 0),
('0-312-87307-7',     'Knife of Dreams',        9.99, 3, 0),
('0-7653-0230-6',     'The Gathering Storm',    9.99, 3, 0),
('978-0-7653-2594-5', 'Towers of Midnight',     9.99, 3, 0),
('978-0-7653-2595-2', 'A Memory of Light',      9.99, 3, 0),
('0-8099-0003-3',     'The Dragonbone Chair',   9.99, 4, 0),
('0-88677-435-7',     'The Stone of Farewell',  9.99, 4, 0),
('0-88677-521-3',     'To Green Angel Tower',   9.99, 4, 0);

INSERT INTO authorship(author_id, book_isbn) VALUES
(1, '0-312-85009-3'),
(1, '0-312-85140-5'),
(1, '0-312-85248-7'),
(1, '0-312-85431-5'),
(1, '0-312-85427-7'),
(1, '0-312-85428-5'),
(1, '0-312-85767-5'),
(1, '0-312-85769-1'),
(1, '0-312-86425-6'),
(1, '0-312-86459-0'),
(1, '0-7653-0629-8'),
(1, '0-312-87307-7'),
(1, '0-7653-0230-6'),
(1, '978-0-7653-2594-5'),
(1, '978-0-7653-2595-2'),
(3, '0-7653-0230-6'),
(3, '978-0-7653-2594-5'),
(3, '978-0-7653-2595-2'),
(8, '0-8099-0003-3'),
(8, '0-88677-435-7'),
(8, '0-88677-521-3');
