<html>
	<head>
	<meta charset="utf-8">
		<title>AdminApp</title>
		<link rel="stylesheet" href="style.css">
	</head>
	
	<body>
		<?php
			include_once 'BDHelper.php';
			$info = new myApp;
			$id = $_GET['user'];
			
			$datos = $info->findUbicaciones($id);
		?>
		<form method='post'>
			<input type="submit" name="back" value="Volver">
		</form>

		<h2 class='user'><?php echo $id ?></h2>
		<center>
			<table class= 'ubications'>
				<tr>
					<th> Ubicación </th>
					<th> Momento </th>
					<th> Nombre </th>
					<th> Acciones </th>
				</tr>
				<?php foreach ($datos as $ubi) {
						$fecha =  (int)$ubi['momento'];
					$mom = date('d-m-Y   H:i:s', $fecha);
					?>
				<tr>
				<form action="" method="post">
					<input type="text" name="id" value=<?php echo $ubi['_id'];?> hidden>
				  <td><?php echo "lon:".$ubi['ubication']['coordinates'][0]." , lat:".$ubi['ubication']['coordinates'][1];?></td>
				  <td><?php echo $mom//$ubi['momento']; ?></td>
				  <td><?php echo $ubi['nombre'];//echo $ubi['hora']; ?></td>
				  <td><input type="submit" name="borrar" value="borrar"></td>
				</form>
				</tr>
			 <?php }?>
				
				
			<table>
			<br>
			<form method='POST'>
				<input type="submit" name="deleteAll" value="Borrar todas"></td>
			</form>
			 
		</center>
		
		<?php
		//Eliminar ubicación
		if(isset($_POST['borrar'])){
			$info->delUbicacion($_POST['id']);
			echo '<script type="text/javascript"> window.location.href="userLocations.php?user='. $id.'"; </script>';
		}
			
		if(isset($_POST['deleteAll'])){
			$info->delUbicacionUser($id);
			echo '<script type="text/javascript"> window.location.href="userLocations.php?user='. $id.'"; </script>';
		}
		
		if(isset($_POST['back'])){
			header("Location: userList.php");
		}
		?>
	</body>
</html>
