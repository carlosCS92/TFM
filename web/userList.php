<html>
	<head>
	<meta charset="utf-8">
		<title>AdminApp</title>
		<link rel="stylesheet" href="style.css">
	</head>
	<body>
		<h2>USUARIOS</h2><br>
		
		<center>
			<form action="" method="post">
				<input type="submit" name='limpiar'  value= "Borrar BBDD">
				<input type="submit" name='copiar' value= "Back-up BBDD">
			</form>
		</center>
		<br>
		
		<table  class='usuarios'>
		  <tr>
			<th>Usuario</th>
			<th>Última conexión</th>
			<th>Activo</th>
		  </tr>
		  <?php
			include_once 'BDHelper.php';
			$info = new myApp;
			$datos = $info->findUsers('');
			foreach ($datos as $user) {
				
				$id = $user['e-mail'];
				if ($user['rol']!=='1992'){
					$fecha =  (int)$user['fechaUso'];
					$uso = date('d-m-Y', $fecha);
			?>
			 <tr>
				  <form action="" method="post">
					<input type='text' name='usuario' value=<?php echo $id; ?> hidden>
					  <td><a href='userLocations.php?user=<?php echo $id ?>'><?php echo $id; ?></a></td>
					  <td><?php echo $uso //$user['fechaUso'] ?></td>
					  <td><input type="submit" name="eliminar" value="Eliminar"></td>
				  </form>
			 </tr>
			 <?php }//fin if
			}//fin for
			 ?>
		  
		</table>
		
		<?php
		
		//Borrar la base de datos por completo
		if(isset($_POST['limpiar'])){
		   $info->drop();
		   echo '<script type="text/javascript"> window.location.href="userList.php"; </script>';
		   
		}
		
		//Hacer un mongodump de la BBDD
		if(isset($_POST['copiar'])){
			$datos = $info->mongodump('C:\xampp\htdocs\adminWeb\copiaBBDD');
			if ($datos==0)
				echo '<script language="javascript">alert("Operación exitosa");</script>'; 
			else
				echo '<script language="javascript">alert("Copia fallida");</script>';
		}
		
		//Eliminar usuario
		if(isset($_POST['eliminar'])){
			$info->delUser($_POST['usuario']);
			echo '<script type="text/javascript"> window.location.href="userList.php"; </script>';
		}
		
		
		?>
		
		
	</body>
</html>