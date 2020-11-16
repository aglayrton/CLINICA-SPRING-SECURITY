//datatables - lista de médicos
$(document).ready(function() {
	moment.locale('pt-BR');
	var table = $('#table-usuarios').DataTable({
		searching : true,
		lengthMenu : [ 5, 10 ],
		processing : true,
		serverSide : true,
		responsive : true,
		ajax : {
			url : '/u/datatables/server/usuarios',
			data : 'data'
		},
		columns : [
				{data : 'id'},
				{data : 'email'},
				{	data : 'ativo', 
					render : function(ativo) {
						return ativo == true ? 'Sim' : 'Não';
					}
				},
				{	data : 'perfis',									 
					render : function(perfis) {
						var aux = new Array();
						$.each(perfis, function( index, value ) {
							  aux.push(value.desc);
						});
						return aux;
					},
					orderable : false,
				},
				{	data : 'id',	
					render : function(id) {
						return ''.concat('<a class="btn btn-success btn-sm btn-block"', ' ')
								 .concat('href="').concat('/u/editar/credenciais/usuario/').concat(id, '"', ' ') 
								 .concat('role="button" title="Editar" data-toggle="tooltip" data-placement="right">', ' ')
								 .concat('<i class="fas fa-edit"></i></a>');
					},
					orderable : false
				},
				{	data : 'id',	
					render : function(id) {
						return ''.concat('<a class="btn btn-info btn-sm btn-block"', ' ') 
								 .concat('id="dp_').concat(id).concat('"', ' ') 
								 .concat('role="button" title="Editar" data-toggle="tooltip" data-placement="right">', ' ')
								 .concat('<i class="fas fa-edit"></i></a>');
					},
					orderable : false
				}
		]
	});
	
	  $('#table-usuarios tbody').on('click', '[id*="dp_"]', function () {
    	var data = table.row($(this).parents('tr')).data();
    	var aux = new Array();
		$.each(data.perfis, function( index, value ) {
			  aux.push(value.id);
		});
		document.location.href = '/u/editar/dados/usuario/' + data.id + '/perfis/' + aux;
    } );	
	
});	

	//quando soltar a tecla vai funcionar o código	
	
$('.pass').keyup(function(){
	if($('#senha1').val() == "" || $('#senha1').val() == ""){
		$('#senha3').attr('readonly', 'readonly');
	}else{
			$('#senha1').val() === $('#senha2').val() ? $('#senha3').removeAttr('readonly') : $('#senha3').attr('readonly', 'readonly'); //bloqueia o campo
	}
});

