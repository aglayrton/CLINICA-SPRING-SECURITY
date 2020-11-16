//Busca a especialidade
$("#especialidade").autocomplete({
	source: function(request, response){
		$.ajax({
			method: "GET",
			url: "/especialidades/titulo",
			data: {
				termo: request.term
			},
			success: function(data){
				response(data);
			}
		});
	
	}

});

//busca os medicos conforme as especialidade
$('#especialidade').on('blur', function() {
    $('div').remove(".custom-radio");
	var titulo = $(this).val();
	if ( titulo != '' ) {			
		$.get( "/medicos/especialidade/titulo/" + titulo , function( result ) {
				
			var ultimo = result.length - 1; 
			
			$.each(result, function (k, v) {
				
				if ( k == ultimo ) {
	    			$("#medicos").append( 
	    				 '<div class="custom-control custom-radio">'	
	    				+  '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>'
						+  '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>'
						+  '<div class="invalid-feedback">Médico é obrigatório</div>'
						+'</div>'
	    			);
				} else {
	    			$("#medicos").append( 
	    				 '<div class="custom-control custom-radio">'	
	    				+  '<input class="custom-control-input" type="radio" id="customRadio'+ k +'" name="medico.id" value="'+ v.id +'" required>'
						+  '<label class="custom-control-label" for="customRadio'+ k +'">'+ v.nome +'</label>'
						+'</div>'
	        		);	            				
				}
		    });
		});
	}
});	

/** 
 * busca os horários livres para consulta conforme a data e o médico.
 * os horários são adicionados a página como um select:option.	
*/
$('#data').on('blur', function () {
	$("#horarios").empty(); //limpa os horários
    var data = $(this).val(); //pega a data selecionada 
    var medico = $('input[name="medico.id"]:checked').val(); //recupera o id do médico
    
    //se a data não foi selecionada
    if (!Date.parse(data)) {
        console.log('data nao selecionada')
    } else {//caso tenha sido, vai ser feito um pedido via get 
    	$.get('/agendamentos/horario/medico/'+ medico + '/data/' + data , function( result ) {
    		$.each(result, function (k, v) {//pega o indice e o valor
    			$("#horarios").append( //cria as option com valores
    				'<option class="op" value="'+ v.id +'">'+ v.horaMinuto + '</option>'
    			);	            			
    	    });
    	});
    }
});

/**
 * Datatable histórico de consultas
*/
$(document).ready(function() {
    moment.locale('pt-BR');
    var table = $('#table-paciente-historico').DataTable({
        searching : false,
        lengthMenu : [ 5, 10 ],
        processing : true,
        serverSide : true,
        responsive : true,
        order: [2, 'desc'],
        ajax : {
            url : '/agendamentos/datatables/server/historico',
            data : 'data'
        },
        columns : [
            {data : 'id'},
            {data : 'paciente.nome'},
            {data: 'dataConsulta', render:
                function( dataConsulta ) {
                    return moment(dataConsulta).format('LLL');
                }
            },
            {data : 'medico.nome'},
            {data : 'especialidade.titulo'},
            {orderable : false,	data : 'id', "render" : function(id) {
                    return '<a class="btn btn-success btn-sm btn-block" href="/agendamentos/editar/consulta/'
                            + id + '" role="button"><i class="fas fa-edit"></i></a>';
                }
            },
            {orderable : false,	data : 'id', "render" : function(id) {
                    return '<a class="btn btn-danger btn-sm btn-block" href="/agendamentos/excluir/consulta/'
                    + id +'" role="button" data-toggle="modal" data-target="#confirm-modal"><i class="fas fa-times-circle"></i></a>';
                }
            }
        ]
    });
});
