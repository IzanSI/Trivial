<?php
$host = "localhost";
$user = "root";
$password = "Ibarra69";
$dbname = "trivialandroid";

$conn = new mysqli($host, $user, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode(["error" => "Error de conexión: " . $conn->connect_error]));
}

$num_preguntas = isset($_GET['num_preguntas']) ? intval($_GET['num_preguntas']) : 1;

$sql = "SELECT id, pregunta, respuesta_correcta, opcion_1, opcion_2, opcion_3, opcion_4 FROM preguntas ORDER BY RAND() LIMIT $num_preguntas";
$result = $conn->query($sql);

if ($result->num_rows > 0) {
    $preguntas = array();
    while ($row = $result->fetch_assoc()) {
        $options = array($row['opcion_1'], $row['opcion_2'], $row['opcion_3'], $row['opcion_4']);
        
        $question = array(
            "id" => $row['id'],
            "pregunta" => $row['pregunta'],
            "respuesta_correcta" => $row['respuesta_correcta'],
            "opciones" => $options
        );
        
        $preguntas[] = $question;
    }
    echo json_encode($preguntas);
} else {
    echo json_encode(array("error" => "No hay preguntas disponibles"));
}

$conn->close();
?>