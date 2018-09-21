<html>
	<head>
		<meta charset="utf-8">
		<title>AdminApp</title>
			<link rel="stylesheet" href="style.css">
	</head>
	<body>
		<?php
		//ñdñdskjbdslavldvb}}}ççç
			include_once 'BDHelper.php';
			$info = new myApp; 
			
			//Compruebo si existe admin
			if (!$info->loginAdmin("admin","adm1n"))
				$info->insertarUser("admin", "", "admin@mail.com", "adm1n", 1992, "", "", "", "", "", "");
			
			
		?>
		
		<FORM class='formulario' name='login' METHOD='POST' ACTION='index.php'>
		  Usuario: <br>
		  <input type="text" name="user">
		  <br><br>
		  Password: <br>
		  <input type="password" name="passwd">
		  <br><br>
		  <input type="submit" value="Entrar">
		</form>
		
		<?php
			if(isset($_POST['user']) && isset($_POST['passwd']))
			{
				if ($info->loginAdmin($_POST['user'],$_POST['passwd']))
				{
					
					header("Location: userList.php");
				}
				else
					echo "Usuario o contraseña incorrectos";
			}
			
		?>
	</body>
	
	
	
</html>