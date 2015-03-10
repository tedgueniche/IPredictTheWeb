/*!
 * Author: Ted Gueniche
 *
 * Released under the MIT license
 */

$(document).ready(function() {

	
	$('#submitButton').click(function() {
		submitForm();
	});
	
	addModel("cpt", "Compact Prediction Tree");
	$("#addButton").click(function() {
		addModel($("#modelField").val(), $("#modelField option:selected").text());
	});
	
	$("#datasetTypeField").change(function() {
		$("#classicDataset").hide();
		$("#customDataset").hide();
		$("#"+ $(this).val()).show();
	});
	
	$(document).tooltip();
});



function submitForm() {

	var models = new Array();
	$('.aModel').each(function() {
		var name = $(this).attr('id');
		models.push(name);
	});
	

	var datasetValue = $('#datasetNameField').val();
	var modelValue = models[0];
	var kfoldValue = $("#kfoldField").val();
	var sequenceCountValue = $("#sequenceCountField").val();
	var req = $.ajax({
	
		type:"POST",
		url: "run.php",
		data: {
			dataset: datasetValue,
			model: modelValue,
			kfold: kfoldValue,
			sequenceCount: sequenceCountValue
		}
	
	}).done(function(data) {
		$('#resultField').val(data); //raw results
		var attrs = [
		{name: 'Accuracy (%)', data: [36.07, 38.45, 31.12, 30.81], atype: 'linear'},
		{name: 'Size (nodes)', data: [484, 30920, 484, 67378], atype: 'logarithmic'},
		{name: 'Training Time (s)', data: [0.076, 0.018, 0.01, 0.356], atype: 'logarithmic'},
		{name: 'Testing Time (s)', data: [0.004, 0.352, 0.001, 0.004], atype: 'logarithmic'},
		];
		
		var columns = ['DG', 'CPT', 'PPM', 'AKOM'];
		showResults(attrs, columns);
		
	}).fail(function(jqXHR, textStatus) {
		alert('fail: '+ textStatus);
		
		
		var attrs = [
		{name: 'Accuracy (%)', data: [36.07, 38.45, 31.12, 30.81], atype: 'linear'},
		{name: 'Size (nodes)', data: [484, 30920, 484, 67378], atype: 'logarithmic'},
		{name: 'Training Time (s)', data: [0.076, 0.018, 0.01, 0.356], atype: 'logarithmic'},
		{name: 'Testing Time (s)', data: [0.004, 0.352, 0.001, 0.004], atype: 'logarithmic'},
		];
		
		var columns = ['DG', 'CPT', 'PPM', 'AKOM'];
		
		showResults(attrs, columns);
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
			field += '<span class="paramValue">'+val+'</span>';
		}
	field += '</span>';
	return field;
}

function addModel(modelValue, modelName) {
	var htmlPre = '<div id="'+ modelValue +'" class="aModel">';
	var htmlPost = '</div>'
	
	var htmlContent = '<span class="modelTitle">'+ modelName + ':</span><br/>';
	if(modelValue == "dg") {
		htmlContent += addParamField("lookahead","Lookahead",4, "A higher value brings a higher accuracy but slows down the algorithm. Recommended: 4", 2,8);
	}
	else if(modelValue == "fom") {
	}
	else if(modelValue == "akom") {
		htmlContent += addParamField("order","Order",5, "A higher value brings a higher accuracy but slows down the algorithm. Recommended: 5", 2,8);
	}
	else {
		htmlContent += addParamField("recursiveDividerMax","Divider",5, "A higher value brings a higher accuracy. Recommended: 5", 0,8);
		htmlContent += addParamField("splitLength","Split Length",12, "A lower value can reduce the execution time and bring a higher accuracy. Recommended: between 10 and 20", 5,50);
	}
	
	htmlContent += '<button class="removeModel">X</button>';
	
	var html = htmlPre + htmlContent + htmlPost;
	$("#modelList").append(html);
	setEvents();
}

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
				type: attr.atype,
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




