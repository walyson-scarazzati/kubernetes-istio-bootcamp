<!DOCTYPE html>  
<html lang="en">  
<head>  
    <meta charset="UTF-8">
    <title>Kubernetes Configmap / Secret</title>
</head>  
<body>
	<div style="background-color: #eeeeee; color: #ff0000">
		<p>This text is taken from hardcoded environment variable</p>
		<h2>${htmlConfigmapHardcoded}</h2>
	</div>

	<hr/>
	
	<div style="background-color: #eeeeee; color: #000000">
		<p>
			For the section below, load background color, text color, 
			and text values from kubernetes configmap / secret.
			The default (when configmap / secret not loaded) are:
			<ul>
				<li>background : white</li>
				<li>text color : black</li>
				<li>text : Default text (k8s Configmap / Secret not loaded)</li>
			</ul>
		</p>
	</div>
	
	<hr/>

	<div style="background-color:${htmlConfigmapBgColor}; color:${htmlConfigmapTextColor};">
	    <h1>This text is from configmap / secret : ${htmlConfigmapTextOne}</h1>

		<style type="text/css">
		.tg  {border-collapse:collapse;border-spacing:0;}
		.tg td{border-color:${htmlConfigmapTextColor};border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;
		  overflow:hidden;padding:10px 5px;word-break:normal;}
		.tg th{border-color:black;border-style:solid;border-width:1px;font-family:Arial, sans-serif;font-size:14px;
		  font-weight:normal;overflow:hidden;padding:10px 5px;word-break:normal;}
		.tg .tg-0lax{text-align:left;vertical-align:top;color:${htmlConfigmapTextColor}}
		</style>
		<table class="tg">
		<tbody>
		  <tr>
		    <td class="tg-0lax">Again, from configmap / secret</td>
		    <td class="tg-0lax">${htmlConfigmapTextTwo}</td>
		  </tr>
		  <tr>
		    <td class="tg-0lax">Also from configmap / secret</td>
		    <td class="tg-0lax">${htmlConfigmapTextThree}</td>
		  </tr>
		  <tr>
		    <td class="tg-0lax">Another configmap / secret</td>
		    <td class="tg-0lax">${htmlConfigmapTextFour}</td>
		  </tr>
		  <tr>
		    <td class="tg-0lax">Still from configmap / secret</td>
		    <td class="tg-0lax">${htmlConfigmapTextFive}</td>
		  </tr>
		  <tr>
		    <td class="tg-0lax">This last one also from configmap / secret</td>
		    <td class="tg-0lax">${htmlConfigmapTextSix}</td>
		  </tr>
		</tbody>
		</table>
    	
    </div>
</body>  
</html>