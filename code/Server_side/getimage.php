<?php
    //open connection to mysql db
    $connection = mysqli_connect("","","","") or die("Error " . mysqli_error($connection));
	$imgtime=$_GET["gal"];
	$sql = "select * from arduino ORDER BY start ASC";
    $result = mysqli_query($connection, $sql) or die("Error in Selecting " . mysqli_error($connection));
	$emparray = array();
	$return_arr = array();
	$start=0;
	$limit=1;
    while($row =mysqli_fetch_assoc($result))
    {
			$caltime=(strtotime($row['start']) - strtotime($imgtime))/ (60);
			if($caltime<=$limit&&$caltime>0){
				$emparray['href'] = "image/".$row['content'];
				array_push($return_arr,$emparray);
			}
			
			
		}
	//echo $emparray;
	echo exit(json_encode($return_arr,JSON_UNESCAPED_SLASHES));
?>