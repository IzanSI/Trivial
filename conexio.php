<?php
$mysql = new mysqli(
    "localhost",
    "root",
    "Ibarra69",
    "trivialandroid"
);

if ($mysql->connect_error) {
    die("Failed to connect: " . $mysql->connect_error);
}
?>