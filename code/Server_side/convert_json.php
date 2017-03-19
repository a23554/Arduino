<?php
    //open connection to mysql db
    $connection = mysqli_connect("","","","") or die("Error " . mysqli_error($connection));
	
	$sql = "select * from arduino ORDER BY start ASC";
    $result = mysqli_query($connection, $sql) or die("Error in Selecting " . mysqli_error($connection));
	$emparray = array();
	$return_arr = array();
	$start=0;
	$limit=1;
    while($row =mysqli_fetch_assoc($result))
    {
		if($start==0){
			$start=(string)$row['start'];
			//$imgstring = htmlentities("<div><a href='getimage.php?id=".$row['id']."'><img src='image/".$row['content']."' style='width:80px;height:80px;'></a></div>");
			//$row['content']="<img src='image/'"+$row['content']+"  style='width:304px;height:228px;'>";
			$emparray['id'] = (string)$row['id'];
			$emparray['start'] = (string)$row['start'];
			$emparray['content'] = "<div class=\"imgclick\" rel=\"".$row['start']."\"><img src=\"image/".$row['content']."\" style=\"width:80px;height:80px;\"></div>";
			array_push($return_arr,$emparray);
			
		}
		else{
			$caltime=(strtotime($row['start']) - strtotime($start))/ (60);  
		     
				if($caltime>$limit){
					//echo "/";
					//echo $caltime;
					$start=(string)$row['start'];
					//$imgstring = htmlentities("<div><a href='getimage.php?id=".$row['id']."'><img src='image/".$row['content']."' style='width:80px;height:80px;'></a></div>");
					//$row['content']="<img src='image/'"+$row['content']+"  style='width:304px;height:228px;'>";
					$emparray['id'] = (string)$row['id'];
					$emparray['start'] = (string)$row['start'];
					$emparray['content'] = "<div class=\"imgclick\" rel=\"".$row['start']."\"><img src=\"image/".$row['content']."\" style=\"width:80px;height:80px;\"/></div>";
					//$emparray['content'] = "<div><a class=\"test\" href=\"image/".$row['content']."\"><img src=\"image/".$row['content']."\" style=\"width:80px;height:80px;\"/></a></div>";
					//echo $imgstring;
					array_push($return_arr,$emparray);
					
				}
				
			}
		}
	//echo $emparray;
	echo exit(json_encode($return_arr,JSON_UNESCAPED_SLASHES));
?>