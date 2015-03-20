//Main controller - called once per page load
$(document).ready(function() {

	//Hooking form submission
	$('#submitButton').click(function() {
		submitForm();
	});
	
	//Adding a model by default
	addModel("cpt", "Compact Prediction Tree");
	$("#addButton").click(function() {
		addModel($("#modelField").val(), $("#modelField option:selected").text());
	});
	
	//Hooking the dataset selector events
	$("#datasetTypeField").change(function() {
		$("#classicDataset").hide();
		$("#customDataset").hide();
		$("#"+ $(this).val()).show();
	});
	
	//Adding the tooltip
	$(document).tooltip();
});

//Fetches the results for a given request id
//This is an asynchronous wait on the request response
function getResults(id) {

	//Loop until a response is available
	var loop = setInterval(function() {

		$.ajax({
	
			type:"GET",
			url: "run.php?action=get&id="+ id,

		}).done(function(data) {
			
			if(data == 'false') {
				console.log("Waiting for request #"+ id);
			}
			else {
				//output the results
				console.log("Got the results");
				console.log(data);

				//stopping the loop
				clearInterval(loop);

				// /{"algorithms": ["dg"], "resuls": [{"name": "Success","data": [24.8]},{"name": "Failure","data": [75.2]},{"name": "No Match","data": [00.000]},{"name": "Too Small","data": [00.000]},{"name": "Overall","data": [24.8]},{"name": "Size (MB)","data": [0.039]},{"name": "Train Time","data": [0.057]},{"name": "Test Time","data": [0.007]}]}

				data = data.split("00.000").join("0");
				var results = JSON.parse(data);
				var attrs = results['resuls'];
				var columns = results['algorithms'];

				showResults(attrs, columns);

			}
			
		}).fail(function(jqXHR, textStatus) {
			alert('fail: '+ textStatus);
		});

	}, 1000);
}

//Send a request to the server
function submitForm() {

	var models = new Array();
	$('.aModel').each(function() {
		var name = $(this).attr('id');

		var params = new Array();
		$(this).find("span.paramValue").each(function() {
	
			params.push({
				name: $(this).attr('name'),
				value: $(this).html()
			});

		});

		console.log(params);

		models.push({
			name: name,
			params: params
		});

		console.log(models);
	});
	

	var datasetValue = $('#datasetNameField').val();
	var modelValue = models;
	var sequenceCountValue = $("#sequenceCountField").val();

	var minS = $(".minSValue").html();
	var prefixSize = $(".prefixSizeValue").html();
	var suffixSize = $(".suffixSizeValue").html();
	var kFold = $(".kFoldValue").html();

	var req = $.ajax({
	
		type:"POST",
		url: "run.php?action=push",
		data: {
			dataset: datasetValue,
			model: modelValue,
			sequenceCount: sequenceCountValue,
			minS: minS,
			prefixSize: prefixSize,
			suffixSize: suffixSize,
			kFold: kFold
		}
	
	}).done(function(data) {
		console.log("request id: "+ data);
		
		var id = data;

		getResults(id);
		
	}).fail(function(jqXHR, textStatus) {
		alert('fail: '+ textStatus);
	});
}


//return an html object representing a parameter for a specific predictor
function addParamField(name, desc, val, help, min, max) {
	var field = '<span>';
		
		field += '<label for="'+name+'">'+desc+':</label>';
		
		if(min === false || max === false) {
			field += '<input title="'+ help +'" type="text" id="'+name+'Field" class="modelField" name="'+name+'" value="'+val+'"/>';
		} else {
			field += '<div title="'+ help +'" min="'+ min +'" max="'+ max +'" id="'+name+'" name="'+name+'" class="slider"></div>';
			field += '<span name="'+ name +'" class="paramValue">'+val+'</span>';
		}
	field += '</span>';
	return field;
}

//Adds a model to the experiment request
function addModel(modelValue, modelName) {
	var htmlPre = '<div id="'+ modelValue +'" class="aModel">';
	var htmlPost = '</div>'
	
	var htmlContent = '<span class="modelTitle">'+ modelName + ':</span><br/>';
	if(modelValue == "dg") {
		htmlContent += addParamField("lookahead","Lookahead",4, "A higher value brings a higher accuracy but slows down the algorithm. Recommended: 4", 2,8);
	}
	else if(modelValue == "ppm") {
	}
	else if(modelValue == "akom") {
		htmlContent += addParamField("order","Order",5, "A higher value brings a higher accuracy but slows down the algorithm. Recommended: 5", 2,8);
	}
	else if(modelValue == "cpt") {
		htmlContent += addParamField("recursiveDividerMax","Divider",5, "A higher value brings a higher accuracy. Recommended: 5", 0,8);
		htmlContent += addParamField("splitLength","Split Length",12, "A lower value can reduce the execution time and bring a higher accuracy. Recommended: between 10 and 20", 5,50);
	}
	else {
		return;
	}
	
	htmlContent += '<button class="removeModel">X</button>';
	
	var html = htmlPre + htmlContent + htmlPost;
	$("#modelList").append(html);
	setEvents();
}

//Hooking events for the models
function setEvents() {
	$('.removeModel').on("click", function() {
		$(this).parent().remove();
	});
	
	$('label').mouseover(function() {
		$('.tooltip').hide();
		$(this).parent().children('.tooltip').fadeIn();
	});
	
	$('label').mouseout(function() {
		$(this).parent().children('.tooltip').fadeOut();
	});
	
	$('.slider').each(function() {
		
		var paramValue = $(this).parent().children('.paramValue');
		
		var minVal = parseInt($(this).attr('min'));
		var maxVal = parseInt($(this).attr('max'));
		var curVal = parseInt(paramValue.html());
		
		$(this).slider({
			min: minVal,
			max: maxVal,
			value: curVal,
			slide: function( event, ui ) {
				paramValue.html(ui.value);
			}
		});
	});
}

//Process and Show the results in the result section
function showResults(data, columns) {
	
	var charts = []
	$containers = $('#ResultGraphs td');
	
	//setting charts
	$.each(data, function(i, attr) {
		charts.push(new Highcharts.Chart({

			chart: {
				renderTo: $containers[i],
				type: 'bar',
				marginLeft: 55,
				marginRight: 15,

			},
			
			plotOptions: {
				series: {
					stacking: 'normal'
				}
			},

			title: {
				text: attr.name,
				align: 'left',
				x: i === 0,
				style: {
					//fontFamily: 'times new roman',
					//fontSize:'18px'
				}
			},

			credits: {
				enabled: false
			},
			
			legend: {
                enabled: false,
				title: {
                    text: 'Models',
                    style: {
                        fontStyle: 'italic'
                    }
                },
                layout: 'horizontal',
                align: 'top',
                verticalAlign: 'top',
                y: 10
				
            },

			xAxis: {
				categories: columns,
				labels: {
					enabled: true
				},
				allowDecimal: true,
				labels: {
					style: {
						//fontFamily: 'times new roman',
						//fontSize:'15px'
					}
				}
			},

			yAxis: {
				title: {
					text: null
				},
				//type: attr.atype,
				type: 'logarithmic',
				labels: {
					style: {
						//fontFamily: 'times new roman',
						//fontSize:'15px'
					}
				}
			},

			series: [attr]

		}));
	});
	
}




