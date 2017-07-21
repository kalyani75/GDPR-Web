	var dataSet = [];
	var pageSize = 1000;
	var topOfPage = 0;
	var tableCreated = false;
	var date;
	$(document).ready(function () {
		$('#more-button').hide();
		//getData(pageSize,topOfPage);
	});

	function getPersonalData(data) {
		dataSet = [];
		$.each(data, function (i, item) {
			var entry = {};
			var entityset = item.entities;
		//	console.log('the Data is:'+data) ;
		//	console.log('the item props is:'+item.Sensitivity+'item :'+item) ;
			entry["FILENAME"] = item.File_Name;
			entry["SENSITIVITY"] =item.Sensitivity;
			
			
			if (entityset.length > 0)
			{
				entry["ENTITIES"] = entityset[0].type;
				entry["EXTRACT"] = entityset[0].text;
			}
			dataSet.push(entry);
			
			for (i = 1; i < entityset.length; i++) {
				var entry = {};
				entry["FILENAME"] = "";
				entry["ENTITIES"] = entityset[i].type;
				entry["EXTRACT"] = entityset[i].text;
				entry["SENSITIVITY"] = "";
				dataSet.push(entry);
			}			
		});
		if (tableCreated == false)
			drawTable();
		else {
			var datatable = $('#data').dataTable().api();
			datatable.clear();
			datatable.rows.add(dataSet);
			datatable.draw();
		}
	}
	function drawTable() {
		tableCreated = true;
		// $('#example').html("");
		var rows,
		sortColumn = 2;
		var table = $('#data').DataTable({
				order : [[2,"desc"]],
        bFilter : false,
				bInfo : false,
				bSort : false,
				pageResize : true,
				dom : 'T<"clear">lfrtip',
				//dom: 'Bfrtip',
				tableTools : {
					"aButtons" : [
						"csv",
						"xls", {
							"sExtends" : "pdf",
							"sPdfOrientation" : "landscape",
							"sPdfMessage" : "Your custom message would go here."
						}
					]
					
				},
				data : dataSet,
				"scrollY" : "400px",
				"scrollX" : true,
				"paging" : false,
				"aoColumns" : [{
						"sWidth" : "5%",
						"data" : "FILENAME",
						"title" : "FILE NAME",
							"sClass" : "dt-body-center"
					}, {
						"sWidth" : "5%",
						"data" : "SENSITIVITY",
						"title" : "SENSITIVITY",
						"sClass" : "dt-body-center",
						"mRender" : renderData
					}, {
						"sWidth" : "5%",
						"data" : "ENTITIES",
						"title" : "PERSONAL INDENTIFIER",
						"sClass" : "dt-body-left"
					}/*, {
						"sWidth" : "5%",
						"data" : "EXTRACT",
						"title" : "EXTRACT",
						"sClass" : "dt-body-center"
					}*/
				]
			})
			
		function renderData(data, type, row) {
			//$(row).find('td:eq(1)').css('color', data);
			//$('td', row).eq(1).addClass(data);
/*			if (typeof data !== 'undefined') {
				return data;
			} else {
				return "";
			}*/
			return data;
		};
        function renderDataAsText( data, type, row ) {
      return "'"+data;
    };

	}
