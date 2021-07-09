<?php
error_reporting(E_ALL);

if (isset($_POST)){
    $data = file_get_contents('php://input');
    // create TCP/IP socket
    $socket = socket_create(AF_INET, SOCK_STREAM, SOL_TCP) or die("Could not create socket \n");
    if ($socket === false){
        echo "socket_create() failed. Reason: " . socket_strerror(socket_last_error()) . "\n";
    }

    // connect to the 'sense_hat_service' port:10001
    $result = socket_connect($socket, 'localhost', 10001) or die("Could not connect to socket \n");
    if ($result === false){
        echo "socket_create() failed. Reason: ($result) " . socket_strerror(socket_last_error($socket)) . "\n";
    }
    // send data
    socket_write($socket, $data, strlen($data));
    $res = socket_read($socket, 16);
    socket_close($socket);
    echo $res;
}
else{
    echo "POST empty";
}
