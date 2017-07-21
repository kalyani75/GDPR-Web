// index.js

var REST_DATA = 'api/todolist';
var REST_ENV = 'api/dbinfo';

var REST_CHAT = 'api/chatinfo';
var REST_RULES = 'api/ruleresult';
var PER_INFO = 'api/personalinfo';
var BATCH_CLASSIFY = 'api/batchClassify';
var REST_FILECONTENT = 'api/call/transcript';
//var REST_FILECONTENT = 'api/updateFile';
var REST_TRANSFORMED_FILECONTENT = 'api/viewTransformedFile';

var KEY_ENTER = 13;
var documents=[];
var cfilename = "" ;

function getPersonalDetails(){

	document.getElementById("selectedFileName").innerHTML=""
		document.getElementById("Sensitivity").innerHTML=""
	clearItem();
	var selFileName = $("input[type='radio'][name='selFile']:checked").val();

	if(selFileName){
		updateResultInfo(selFileName);
		
	}else{alert("Please Select a File Name");}
	
}
function classify(node) {
	
	var files = node.previousSibling.files;
	var file = node.previousSibling.files[0];
	console.log('Files - ' + file);
	//if file not selected, throw error
	if (!file) {
		alert("Files not selected for classification... \t\t\t\t \n\n - Choose files to classify. \n - Then click on Classify button.");
		return;
	}
	documents = [] ;
	for (var k = 0; k < files.length; k++) {

	    // get item
	    file = files.item(k);
	    //or
	    file = files[k];

	    var form = new FormData();
		form.append("file", file);
		var queryParams ="";
	//	console.log(file.type) ;
	//	console.log('k: '+k+' length: '+files.length) ;
		queryParams+= "&filename="+file.name;
		queryParams+= "&filetype="+file.type;
		xhrAttach("attach?"+queryParams, form, function(data){
			console.log('Return After Classify - ' + data.Sensitivity);
			documents.push(data) ;
		//	console.log('k: '+k+' length: '+files.length+' doc length: '+documents.length) ;
			if (documents.length == files.length)
				{
				var imgn = 'images/loader.gif';
			//	document.getElementById('ruleresults').hide();
				document.getElementById("loadingresults").innerHTML = "<img src='" + imgn +"'>&nbsp; &nbsp;Loading classification results  ..Please wait.."
				
				getPersonalData(documents) ;
			document.getElementById("loadingresults").innerHTML = "";
				}
		}, function(err) {
			console.error(err);
		});
		
	
/*		var xhr = new XMLHttpRequest();
		// Add any event handlers here...
		xhr.open('POST', BATCH_CLASSIFY, true);
		xhr.setRequestHeader("Content-Type","multipart/form-data");
		xhr.send(form);*/

	}
	console.log('documents is:'+documents) ;
	//getPersonalData(documents) ;
}

function viewTransformedFileContent(chatfilename){

	
		   
		xhrGet(
				REST_TRANSFORMED_FILECONTENT + '?id=' + chatfilename,
				function(data) {
					console.log(data);
					
					//Adding Header 

					document.getElementById("transformedFilecontentTitle").innerHTML ="Transcript File Content : " + chatfilename
					
					var json = data.filecontent;
					document.getElementById("transfromedViewcontent").innerHTML = JSON.stringify(json, undefined, 2);
					
					console.log(json);
					console.log("successfully viewed file conetnt...");

				}, function(err) {

					console.error(err);
					document.getElementById("viewTransformedFileLoad").innerHTML = "ERROR LOADING FILE CONTENT";

				});
	

}
function viewFileContent(text){

	clearItem();
	
	
	    	
	    	$("#viewcontent").html(text);
	    	
	    	
	   
}
/*
function viewFileContent(chatfilename){

	clearItem();
	
	 $.get( REST_FILECONTENT + '?id=' + chatfilename)
	    .done( function(data){
	    	
	    	if ( chatfilename.indexOf(".json") > 0 ){
				var d = window.JSON ? JSON.parse(data) : eval('(' + data + ')');
				var json = d.filecontent;
				
				$("#viewcontent").html( JSON.stringify(json, undefined, 2) );
				
				
			}else {
				$("#viewcontent").html( data);
			}
	    	
	    	$("#filecontentTitle").html ( chatfilename)
	    	
	    }).fail( function(){
	    	$("#viewFileLoad").html("ERROR LOADING CHAT TRANSCRIPT FILE CONTENT");
	    })
}
*/
//Method to fetch the Chat Transcript Files
function updateADocInfo() {
	
	xhrGet(
			REST_CHAT,
			function(data) {
				document.getElementById("loading").innerHTML = ""
				
				document.getElementById("subTitle").innerHTML = "<a href='#' class='btn btn-info btn-default active'  onclick='getPersonalDetails()'>Get Personal Details</a>";
				console.log(data);
				
				//Adding Header 

				var table = document.getElementById("transcript");
				$("#transcript").addClass("table").addClass("table-striped");
				
				var header = table.createTHead();
				var row = header.insertRow(0);
				var cell = row.insertCell(0);
				cell.innerHTML = "<b>  Select</b>";
				var cell1 = row.insertCell(1);
				cell1.innerHTML = "<b>File Name</b>"

					
				//Dynamically populate table based on transcript files
				var filename = data.name;
				for (i = 0; i < filename.length; i++) {
					addChatFiles(filename[i], false, i);
					//console.log(filename[i]);

				}
			    $("#tablescroll").show();
				console.log("successfully added chat Files....");

			}, function(err) {

				console.error(err);
				document.getElementById("loading").innerHTML = "ERROR LOADING FILES";

			});

}

function addChatFiles(item, isNew, id) {
	var table = document.getElementById('transcript');
	
	var row = document.createElement('tr');
	table.lastChild.appendChild(row);	
	row.setAttribute('chatid', item);
		
	//Insert columns
	 var cell1 = row.insertCell(0);
	 cell1.className = "tbradio";
	 var r1 = document.createElement('input');
     r1.type = "radio";
     r1.name = "selFile" ;
     r1.value = item ;
     r1.id = item;
     cell1.appendChild(r1);
     
     var cell2 = row.insertCell(1);
     cell2.className = "tbfileName";
 	
 	if (item) {		cell2.innerHTML = item;	}
	row.isNew = !item || isNew;
	
}
function updateBatchInfo(chatfilename) {
	document.getElementById("checkResults").innerHTML = "";
	document.getElementById("browse").innerHTML ="<br><input type=\"file\" name=\"my_file[]\" id=\"upload_file\" multiple><input width=\"100\" type=\"submit\" class= \"butn\" value=\"Classify\" onClick='classify(this)'>";
	
}

//method to populate the Transcript Rule results
function updateResultInfo(chatfilename) {

	//Enable the loading message
	var imgn = 'images/loader.gif';
	document.getElementById("loadingresults").innerHTML = "<img src='" + imgn +"'>&nbsp; &nbsp;Fetching Personal details from the transcript  ..Please wait.."

	//Clean up the previous results
	var table = document.getElementById('ruleresults');
	$("#ruleresults").addClass("table").addClass("table-stripped")
	
	while (table.rows.length > 0) {
		table.deleteRow(0);
	}
	// Invoke the rest api to get the rule results and dynamically populate the table
	xhrGet(
			//REST_RULES + '?id=' + chatfilename,
			PER_INFO + '?id=' + chatfilename,
			function(data) {
				console.log(data);
				
				//Enable Div title
				document.getElementById("checkResults").innerHTML = "<h3>Personal Details</h3>"
				document.getElementById("loadingresults").innerHTML = ""

					var text = data.text;
				var sensitivity = "Sensitvity - "+data.Sensitivity;
				var entityset = data.entities;
				document.getElementById("selectedFileName").innerHTML = "<h3>" + chatfilename + "</h3>"
				document.getElementById("Sensitivity").className=sensitivity ;

				document.getElementById("Sensitivity").innerHTML = "<h3>" + sensitivity + "</h3>"
		

				//dynamically populate the table based on the Rules Results
				//var entityResult = document.getElementById("ruleResults");
				for (i = 0; i < entityset.length; i++) {
					console.log(entityset[i].type,entityset[i].text);
					var row = document.createElement('tr');
					row.innerHTML = "<td style='width:20%; text-align:left;'>"+entityset[i].type+"</td><td style='width:80%;text-align:left;'>"+entityset[i].text+"</td>";
					//row.setAttribute('ruleid', id);

					table.lastChild.appendChild(row);

				}
				//to work on later
				viewFileContent(text);
				//viewFileContent(chatfilename);
			/*	viewTransformedFileContent(chatfilename);*/
				console.log("updateResultInfo success....");

			}, function(err) {
				console.error(err);
				document.getElementById("loadingresults").innerHTML = "ERROR FETCHING RESULTS";

				//document.getElementById("loading").innerHTML = "ERROR";

			});

}

function addResultsFiles(item,ruletext, result, reason,sequenceval,isNew, id) {
	
	var table = document.getElementById('ruleresults');

	var row = document.createElement('tr');
	row.innerHTML = "<td style='width:50px'><a href='http://demochatcompliance.mybluemix.net/view/rule/"+item+"' ></a></td><td style='width:300px'></td><td style='width:50px'></td><td style='width:450px'></td>";
	row.setAttribute('ruleid', id);

	table.lastChild.appendChild(row);


	var textarea = row.childNodes[0];
	
	if (item) {row.firstChild.firstChild.innerHTML =item };
	/*if (item) {	textarea.innerHTML = item;	}*/
	
	var ruleTextArea=row.childNodes[1];
	if (ruletext) {	ruleTextArea.innerHTML = ruletext;	}

	var resultArea = row.childNodes[2];
	if (result) {
			resultArea.innerHTML = result;  
			if (result == "yes" || result == "no" ) {
				resultArea.classList.add(result)
			}else { 
				resultArea.classList.add("na")
			} 	
	}
	
	var reasonArea = row.childNodes[3];
	if (reason) {reasonArea.innerHTML = reason +" :  <br>" +sequenceval;	}
}

/*updateServiceInfo();*/

function clearItem(){

	document.getElementById("filecontentTitle").innerHTML =""
	document.getElementById("viewcontent").innerHTML =""
	document.getElementById("transformedFilecontentTitle").innerHTML =""
	document.getElementById("transfromedViewcontent").innerHTML =""
}



$(document).ready(function () {
        $("#tablescroll").hide();
});

loader();

function loader(){

	 $("#tablescroll").hide();
}

function editContent(){
	$("#viewcontent").removeAttr("disabled") 
	$("#saveContent").removeClass("disabled")
}

function saveTranscript(){
	$("#viewcontent").attr("disabled", "disabled")
	var selFileName = $("input[type='radio'][name='selFile']:checked").val();
	$.ajax({
		  method: "POST",
		  url: REST_FILECONTENT +  '?id=' + selFileName,
		  data: { transcript: $("#viewcontent")[0].value}
		}).done(function( msg ) {
		    alert( "Data Saved: " + msg );
		  }).fail(function(msg){
			  alert(" There was an error while saving")
		  });
	
}

updateADocInfo();
updateBatchInfo();
$(document).ready(function(){
	$('ul.tabs li').click(function(){
		var tab_id = $(this).attr('data-tab');
		$('ul.tabs li').removeClass('current');
		$('.leftHalf').removeClass('current');
		$(this).addClass('current');
		$("#"+tab_id).addClass('current');
		if(tab_id==="leftHalf-Multi") {
			$('#single-file').hide();
			$('#multi-files').show();
		} else if(tab_id==="leftHalf-Single") {
			$('#multi-files').hide();
			$('#single-file').show();
		}
	});
});
