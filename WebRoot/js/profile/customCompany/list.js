
$(document).ready(function() {
	document.title = '报关企业查询 | '+document.title;

    $('#menu_order').addClass('active').find('ul').addClass('in');
    
    $("#beginTime_filter").val(new Date().getFullYear()+'-'+ (new Date().getMonth()+1));
    
	  //datatable, 动态处理
    var dataTable = $('#eeda-table').DataTable({
        "processing": true,
        "searching": false,
        //"serverSide": true,
        "scrollX": true,
        "scrollY": "300px",
        "scrollCollapse": true,
        "autoWidth": false,
        "language": {
            "url": "/yh/js/plugins/datatables-1.10.9/i18n/Chinese.json"
        },
        //"ajax": "/damageOrder/list",
        "columns": [
            { "data": "SHOP_NO"},
            { "data": "SHOP_NAME", 
                "render": function ( data, type, full, meta ) {
                    return "<a href='/customCompany/edit?id="+full.ID+"'target='_blank'>"+data+"</a>";
                }
            },
            { "data": "LEGAL_PERSON"},
            { "data": "CONTACT_PERSON"}, 
            { "data": "CONTACT_PHONE"}, 
            { "data": "COMPANY_PHONE"},
            { "data": "CREATOR_NAME"}, 
            { "data": "CREATE_STAMP"}
        ]
    });

    
    $('#resetBtn').click(function(e){
        $("#orderForm")[0].reset();
    });

    $('#searchBtn').click(function(){
        searchData(); 
    })

   var searchData=function(){
        var shop_name = $("#shop_name").val();
        var start_date = $("#create_stamp_begin_time").val();
        var end_date = $("#create_stamp_end_time").val();
        
        /*  
            查询规则：参数对应DB字段名
            *_no like
            *_id =
            *_status =
            时间字段需成双定义  *_begin_time *_end_time   between
        */
        var url = "/customCompany/list?shop_name="+shop_name
             +"&create_stamp_begin_time="+start_date
             +"&create_stamp_end_time="+end_date;
        dataTable.ajax.url(url).load();
    };
    

} );