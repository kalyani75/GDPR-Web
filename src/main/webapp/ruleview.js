
var REST_RULECONFIG = 'api/ruleconfig';

var KEY_ENTER = 13;

// Method to fetch the Chat Transcript Files
function updateRuleConfig() {
	
	var imgn = 'images/ruleload.gif';
	document.getElementById("loadingresults").innerHTML = "<img src='" + imgn +"'>&nbsp; &nbsp;Fetching the Rule Configuration  ..Please wait.."


	var query = window.location.search.substring(1);

	xhrGet(
			REST_RULECONFIG + '?' + query,
			function(data) {
				console.log(data);
				document.getElementById("loadingresults").innerHTML = ""
				// get the keys:
				var keys = Object.keys(data);
				var parentDiv = document.getElementById("rulebg");

				if (keys.length > 0) {
					// Row for Rule name
					addRowItem(parentDiv, data.number, "Rule Name ", "number");
					addRowItem(parentDiv, data.text, "Rule Description ", "text");
					var direction =data.direction;
					traverseDirectionArray("Direction ", direction, parentDiv);
					//Row for Sequence
					var sequences = data.sequences;
					traverseSequenceArray("Scenario ", sequences, parentDiv);
				
				}

			},
			function(err) {

				console.error(err);
				document.getElementById("loadingresults").innerHTML = "ERROR Fetching Rule Details";

			});

}

function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}

function traverseSequenceArray(rowname, arr, parentDiv) {
	arr
			.forEach(function(x) {
				console.log("-----------> x :" + x);
				for ( var key in x) {
					if (x.hasOwnProperty(key)) {
						console.log(" Key --> " + key + ": Value :" + x[key]);
						if (key === 'id')
							addRowItem(parentDiv, x[key], rowname,
									rowname + key);
						if (key === 'description') 
							addRowItem(parentDiv, x[key], "Description", key);
							if (key === 'additionalChecks')
								addRowItem(parentDiv, x[key],
										"Additional Checks", key);
					
					}
				}

			});
}

function traverseDirectionArray(rowname, arr, parentDiv) {
	
	if(arr.length >0){
		var textValue = arr[0];
		console.log("-----------> arr.lenght :" + arr.length);
		for(i=1;i<arr.length;i++){
			textValue = textValue + ',' + arr[i];
		}	
		addRowItem(parentDiv, textValue, rowname, rowname);
	}
	else
		addRowItem(parentDiv, "ALL", rowname, rowname);
	
}

//Add row as two divs : label and Value
function addRowItem(parentDiv, item, key, id) {

	// Create div for key
	addRowDivName(parentDiv, key, id);
	// Create div for Value
	addRowDivText(parentDiv, key, item, id);
}

//Add row as two divs : label and Value
function addRowDivName(parentDiv, key, id) {
	// Create div for key
	var iDivLb = document.createElement('div');
	iDivLb.id = id;
	iDivLb.className = 'ruleLabel';
	parentDiv.appendChild(iDivLb);
	iDivLb.innerHTML = key;
	console.log("Created Div Label --->" + key);
}

//Add row as two divs : label and Value
function addRowDivText(parentDiv, key, item, id) {

	// Create div for key
	var iDivVal = document.createElement('div');

	var att = document.createAttribute("contenteditable");

	iDivVal.setAttributeNode(att);
	iDivVal.id = "input";
	iDivVal.className = 'ruleValue';
	parentDiv.appendChild(iDivVal);
	if (item)
		iDivVal.innerHTML = item;
	else
		iDivVal.innerHTML = '';

	console.log("Created Div Label --->" + item);
}

function isArray(o) {
	return Object.prototype.toString.call(o) === '[object Array]';
}

function loader() {

}

updateRuleConfig();
