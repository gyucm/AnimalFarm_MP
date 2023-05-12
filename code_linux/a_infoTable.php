<!DOCTYPE html>
<html>
<head>
	<meta charset = "UTF-8">
	<meta http-equiv = "refresh" content = "30">
	<style type = "text/css">
		.spec{
			text-align:center;
		}
		.con{
			text-align:left;
		}
		</style>
</head>

<body>
	<h1 align = "center">Animal Information</h1>
	<div class = "spec">
		# <b>LOVE YOUT ANIMAL</b>
		<br></br>
	<form name="Filter" method="POST">
    	<input type="date" name="dateFrom" value="<?php echo date('Y-m-d'); ?>" min="2021-08-07" />
    	<input type="date" name="dateFrom2" value="<?php echo date('Y-m-d'); ?>" />
    	<input type="submit" name="submit" value="Search"/>
</form>
	</div>

	<table border = '1' style = "width = 30%" align = "center">
	<tr align = "center">
		<th>SEQUENCE</th>
		<th>NAME</th>
		<th>DATE</th>
		<th>TIME</th>
		<th>WEIGHT</th>
		<th>HEART</th>
	</tr>

	<?php
        	$new_date = date('Y-m-d', strtotime($_POST['dateFrom']));
        	$new_date1 = date('Y-m-d', strtotime($_POST['dateFrom2']));
        	if($new_date == $new_date1) {
               		$new_date = date('Y-m-d');
                	$new_date1 = $new_date;
                	$printDate = 0;
                	$query = "select sequ, NAME, date, time, weight, heart from a_info where date(date) = date('".$new_date."')";
        	}
        	else
        	{
                	$printDate = 1;
                	$query = "select sequ, NAME, date, time, weight, heart from a_info where date(date) between date('".$new_date."') and date('".$new_date1."')";
        	}

		$conn = mysqli_connect("localhost", "pi", "raspberry");
		mysqli_select_db($conn, "iotdb");
        	$result = mysqli_query($conn, $query);
		while($row = mysqli_fetch_array($result))
		{
			echo "<tr align = center>";
			echo '<th>'.$row['sequ'].'</td>';
			echo '<th>'.$row['NAME'].'</td>';
			echo '<th>'.$row['date'].'</td>';
			echo '<th>'.$row['time'].'</td>';
			echo '<th>'.$row['weight'].'</td>';
				echo '<th>',$row['heart'].'</td>';
			echo "</tr>";

		}
		mysqli_close($conn);

	?>
	</table>
</body>
</html>
