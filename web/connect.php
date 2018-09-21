<?php

		
	include_once 'BDHelper.php';
	$con = new myApp();
	
	$op = $_REQUEST['operation'];
	
	
				
	//Añadir un nuevo usuario	
	if ($op === 'nuevo')
	{
		$data = $con->findUsers($_REQUEST['ma']);
		
		foreach ($data as $d)
		{
			if ($d["e-mail"] = $_REQUEST['ma'])
				echo "e-mail existente";
				return;
		}
		$con->insertarUser($_REQUEST['nom'], $_REQUEST['ap'], $_REQUEST['ma'], '', 'user', $_REQUEST['wa'], $_REQUEST['te'], '', $_REQUEST['fn'], getdate()[0], "");
		echo "insertado con exito\n";
		//insertarUser($nombre, $apellido, $email, $pssd, $rol, $wa, $tlf, $image, $fechaNac, $fechaUso, $comp)
	}
	
	//Se actualizan las aptitudes o inquietudes
	else if ($op === 'act')
	{
		$con->addComp($_REQUEST['ma'], $_REQUEST['con']);
		//echo "Actividades exitosas";
	}
	
	//Modifica un usuario
	else if($op === 'change')
	{
		$con->alterUser($_REQUEST['ma'], $_REQUEST['nom'], $_REQUEST['ap'], '', $_REQUEST['te'], $_REQUEST['wa'], '', $_REQUEST['fn'], getdate()[0], $_REQUEST['con'], 'user'); 
		echo "cambio con exito";
		//alterUser(($email, $nombre, $apellidos, $pssd, $tlf, $wa, $image, $fechaNac, $fechaUso, $comp)
	}
	
	//Busca usuarios cercanos en el mismo momento
	else if($op === 'buscarU')
	{//($user, $momento, $long, $lat)
		$con->addUbicacion($_REQUEST['ma'], $_REQUEST['lat'], $_REQUEST['lon'], getdate()[0], "");
		$users = $con->findNearUsers($_REQUEST['ma'], getdate()[0], $_REQUEST['lon'], $_REQUEST['lat']);
		$data = [];
		foreach($users as $u)
		{
			$data[] = array ("nombre" => $u["Nombre"],
							"apellido" => $u["Apellidos"],
							"mail" => $u["e-mail"]);
		}
		echo json_encode($data);
	}
	
	//Busca usuarios en una posicion en un momento elegido
	else if ($op == 'buscarUserS')
	{
		$ubication = $con->findUbication($_REQUEST['nom'], $_REQUEST['ma']);

		$momento = 0;
		$lat = 0;
		$lon = 0;
		
		foreach ($ubication as $u)
		{
			
			$momento = $u["momento"];
			$lat = $u["ubication"]["coordinates"][1];
			$lon = $u["ubication"]["coordinates"][0];
		}
		if($momento == 0)
			return;
		
		$users = $con->findNearUsers($_REQUEST['ma'], $momento, $lon, $lat);
		
		$data = [];
		foreach($users as $u)
		{
			$data[] = array ("nombre" => $u["Nombre"],
							"apellido" => $u["Apellidos"],
							"mail" => $u["e-mail"]);
		}
		echo json_encode($data);
	}
	
	//Busca las ultimas ubicaciones de un usuario
	else if($op === 'buscarUbi')
	{
		$doc = $con->findUbicaciones($_REQUEST['ma']);
		
		$data = [];
		foreach($doc as $mongo)
		{
			//echo $mongo["user"];
			if ($mongo["nombre"] == "")
				continue;
			
			$data[] = array ("nombre"=>$mongo["nombre"],
							"momento"=>$mongo["momento"]);
							
		//$fp = fopen ("C:/xampp/htdocs/adminWeb/temp.txt", "a");
		//fwrite($fp, $mongo["momento"]. "  ". $mongo["user"]);
		//fclose($fp);
		}
		
		$json = json_encode($data);

		echo $json;
	}
	
	//Añade una ubicacion
	else if($op === 'ubicacion')
	{//addUbicacion($user, $latitud, $longitud, $momento)
		$con->addUbicacion($_REQUEST['ma'], $_REQUEST['lat'], $_REQUEST['lon'], getdate()[0], $_REQUEST["nom"]);
	}
	
	
	//Muestra la información de un usuario
	else if ($op === 'usuario')
	{
		$doc = $con->findUsers($_REQUEST['user']);
		
		foreach($doc as $u)
		{
			$data[] = array ("nombre" => $u['Nombre'],
							"apellidos" => $u['Apellidos'],
							"mail" => $u['e-mail'],
							"wa" => $u['wa'],
							"tlf" => $u['telefono'],
							"fechaNac" => $u['fechaNac']);
		}
		$json = json_encode($data);
		echo $json;
	}
	
	
	//Añade un contacto a favoritos
	else if ($op === 'newContact')
	{
		$con->addFavorito($_REQUEST["ma"], $_REQUEST["favorito"], $_REQUEST["nombre"]);
		
	}
	
	
	//Elimina un favorito
	else if ($op === 'delContact')
	{
		$con->delFavorito($_REQUEST["ma"], $_REQUEST["favorito"]);
		
	}
	
	//Listadp de favoritos
	else if ($op === 'contactos')
	{
		$datos = $con->findFavoritos($_REQUEST["ma"]);
		//$data[];/
		foreach($datos as $d)
		{
			$data[] = array("nombre" => $d["nombre"],
							"mail" => $d["fav"]);
		}
		$json = json_encode($data);
		echo $json;
	}
	
	
	else{
		echo $op;
		
	}
		
	
	
	
?>