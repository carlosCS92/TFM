<?php
	
	class myApp{
		private $datos;
		private $conexion;
		private $bd;
		
		
		function myApp(){
			$conexion = new MongoClient();
			$this->bd = $conexion->info;
		}
		
		//Comprueba el usuario y contraseña introducidos como administrador
		function loginAdmin($nombre, $pwd)
		{
			$coleccion = $this->bd->users;
			
			$datos = $coleccion->findOne(array('Nombre' => $nombre, 'password' => $pwd, 'rol' =>'1992'));
			
			return $datos;
		}
		
		//Inserta un usuario
		function insertarUser($nombre, $apellido, $email, $pssd, $rol, $wa, $tlf, $image, $fechaNac, $fechaUso)
		{
			$coleccion = $this->bd->users;
			//$colecUbication = $this->bd->ubication;
			
			$doc = array(
				"Nombre" => "$nombre",
				"Apellidos" => "$apellido",
				"e-mail" => "$email",
				"wa" => "$wa",
				"password" => "$pssd",
				"rol" => "$rol",
				"telefono" => "$tlf",
				"image" => "$image",
				"fechaNac" => "$fechaNac",
				"fechaUso" => "$fechaUso",
				//"comp"=>array($comp)
			);
			$coleccion->insert($doc);
			//$colecUbication->insert(array("id"=>$email, "ubi"=>array()));
			
			return true;
		}//insertarUser
		
		function addComp ($email, $comp){
			$con = array();
			$con = explode( "," , $comp );
			$coleccion = $this->bd->users;
			
			$coleccion->update(array("e-mail" => "$email"), array('$unset'=>array("conocimientos"=> 1)));
			//$coleccion->update(array("e-mail" => "$email"), array('$set'=>array("conocimientos"=>array("$comp"))));
			$coleccion->update(array("e-mail" => "$email"), array('$set'=>array("conocimientos"=>$con)));
		}//addComp
		
		
		/*
			* Si el par?metro email no es una cadena vac?a, muestra todos los usuarios
			* En caso contrario, muestra s?lo el usuario con email introducido
		*/
		function findUsers($email)
		{
			$coleccion = $this->bd->users;
			
			if ($email == '')//Busco todos
			{
				$datos = $coleccion->find();
				
			}
			else{//busco por email
			
				$datos = $coleccion->find(array('e-mail' => $email));
				
			}
			return $datos;	
		}//findUser
		
		function delUser($email)
		{
			$coleccion = $this->bd->users;
			$coleccion->remove(array("e-mail" => "$email"));
		}//deluser
		
		function alterUser($email, $nombre, $apellidos, $pssd, $tlf, $wa, $image, $fechaNac, $fechaUso, $comp, $rol)
		{
			$coleccion = $this->bd->users;
			$doc = array(
				"e-mail" => "$email",
				"wa" => "$wa",
				"password" => "$pssd",
				"Nombre" => "$nombre",
				"Apellidos" => "$apellidos",
				"rol" => "$rol",
				"telefono" => "$tlf",
				"image" => "$image",
				"fechaNac" => "$fechaNac",
				"fechaUso" => "$fechaUso",
				//"conocimientos" => array($comp)
			);
			$coleccion->update(array('e-mail'=> "$email"),$doc);
			$this->addComp($email, $comp);
			
			return true;
		}//alterUser
		
		//@user correo del usuario
		function addUbicacion($user, $latitud, $longitud, $momento, $nombre)
		{
			$coleccion = $this->bd->ubication;
			$doc= array(
				"user" => "$user",
				//"latitude" => "$latitud",
				//"longitude" => "$longitud",
				"ubication" => array("type"=> "Point", "coordinates"=>array($longitud,$latitud)),
				"momento" => "$momento",
				//"hora"  => "$hora"
				"nombre" => "$nombre"
			);
			$coleccion-> insert($doc);
			return true;
		}//addUbicacion
		
		
		//Muestra todas las ubicaciones del usuario
		function findUbicaciones($user)
		{
			//echo $user;
			$coleccion = $this->bd->ubication;
			$datos = $coleccion-> find(array("user"=>$user));
			
			
			
			return $datos;
		}//findUbicaciones
		
		//Borra una ubicación
		function delUbicacion($id)
		{
			$coleccion = $this->bd->ubication;
			$coleccion->remove(array("_id" => new MongoId($id)));
		}//delUbicacion
		
		function findUbication($nombre, $mail){
			/*$fp = fopen("C:/xampp/htdocs/adminWeb/temporal.txt","a");   
                fwrite($fp, "nombre" . print_r($nombre,1) . "\n" ); 
				fwrite($fp, "mail" . print_r($mail,1) . "\n" ); 
                fclose($fp);*/
			$coleccion = $this->bd->ubication;
			$ret = $coleccion->find(array("user"=>"$mail", "nombre" =>"$nombre"));
			
			return $ret;
		}
		
		//Hace una copia de seguridad de la BBDD
		function mongodump($folder) 
		{
			$command = "mongodump -d info --out ".$folder;
			
			exec($command, $output, $return_value);
			
			return $return_value;	
		}
		
		//Borra todas las ubicaciones de un usuario
		function delUbicacionUser($user)
		{
			$coleccion = $this->bd->ubication;
			$coleccion->remove(array("user"=>$user));
		}
		
		//Elimina la BBDD
		function drop() {
			$this->bd->command(array("dropDatabase" => 1));
		}
		
		
		//Busca los usuarios cercanos en funcion de los gustos similares
		function findNearUsers($user, $momento, $long, $lat)
		{
			$coleccion = $this->bd->ubication;
			$antes = strtotime('-5 minute' , $momento);
			$despues = strtotime('+5 minute' , $momento);
			
			$long = floatval($long);
			$lat = floatval($lat);
			
			$doc=array(
				"ubication"=> array("\$geoWithin"=>array("\$center"=>array(array($long,$lat),1000))),
				"momento"=> array('$gte' => "$antes" , '$lte' => "$despues")
			);
			
			$cercanos =  $coleccion->find($doc);//Busco los usuarios cercanos
			$use = $this->findUsers($user); //Me quedo con la info del usuario
			$usuario = null; //Inicializo el usuario de busqueda a null
			
			foreach($use as $u)
			{
				$usuario = $u;//Almaceno la información en $usuario
			}
			
			//echo $cercanos;
			$ret = [];//Array de retorno
			//echo "{aaa!}";
			
			$fp = fopen("C:/xampp/htdocs/adminWeb/temporal.txt","a");   
                fwrite($fp, "numero " . print_r($usuario,1) . "\n" ); 
				fwrite($fp, "cusgfghgh" . print_r(count($cercanos),1) . "\n" ); 
                fclose($fp);
				
				
			foreach ($cercanos as $cerca){
				$fp = fopen("C:/xampp/htdocs/adminWeb/temporal.txt","a");   
                fwrite($fp, "nombre " . print_r($cerca["Nombre"],1) . "\n" ); 
				fwrite($fp, "mail" . print_r($cerca). "\n" ); 
                fclose($fp);
			}
			
			foreach($cercanos as $candidato)//Recorro la lista de usuarios cercanos
			{
				
				//echo " candidato ".$candidato["user"];
				//echo "chachi";
				$cono = $this->findUsers($candidato["user"]); //Saco la info del candidato
				//echo "chachi2";
				foreach ($cono as $c)//recorro todos los candidatos
				{
					//echo "chachi3";
					$result = array_intersect($usuario["conocimientos"], $c["conocimientos"]); //Comparo los arrays de los conocimientos
					if(!empty($result))//Si hay alguna coincidencia
					 //echo $c["e-mail"];
						//array_push($ret, $c);//Inserto usuario en array
						$ret[] = $c;
				}
			}
			
			return $ret;
			
			///Consulta de geo location
			//db.ubication.find({ubication:{$geoWithin:{$center:[[-4.2563,40.25801],10]}}}).pretty();
			//Con el día
			//db.ubication.find({ubication:{$geoWithin:{$center:[[-4.2563,40.25801],10]}},fecha:"29/04/2018"});

		}//findnearusers
		
		
		//Añadir contacto a favoritos
		function addFavorito ($user, $contacto, $nombre){
			$coleccion = $this->bd->favoritos;
			
			$doc=array(
				"usuario"=>"$user",
				"fav"=>"$contacto",
				"nombre"=>"$nombre"
			);
			$coleccion->insert($doc);
		}//addFavorito
		
		//Borra a un contacto de la lista de favoritos
		function delFavorito($user, $contacto){
			$coleccion = $this->bd->favoritos;
			
			$doc=array(
				"usuario"=>"$user",
				"fav"=>"$contacto"
			);
			$coleccion->remove($doc);
		}//delFavorito
		
		function findFavoritos($user)
		{
			$coleccion = $this->bd->favoritos;
			
			return $coleccion->find(array("usuario"=>"$user"));
			
			
		}
		
	}//class myApp
?>