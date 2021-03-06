
$(document).ready(function() {

	document.title = order_no + ' | ' + document.title;
	
	 //form表单校验
	$("#itemForm").validate({
        rules: {
        	amount:{
        		min:0
        	}
        }    
	});
	 
    //------------save
    $('#saveBtn').click(function(e){
        //阻止a 的默认响应行为，不需要跳转
        e.preventDefault();
        //提交前，校验数
        
        if(!$("#itemForm").valid()){
            return;
        }
        
        $(this).attr('disabled', true);

        var items_array = waveOrder.buildItemDetail();
        
        var order={}
        order.id = $('#order_id').val();
        order.item_list = items_array;   
        
        //异步向后台提交数据
        $.post('/waveOrder/save', {params:JSON.stringify(order)}, function(data){
            var order = data;
            if(order.ID>0){
                $("#order_id").val(order.ID);
                $("#order_no").val(order.ORDER_NO);
                $("#creator_name").val(order.CREATOR_NAME);
                $("#create_stamp").val(order.CREATE_STAMP);
                contactUrl("edit?id",order.ID);
                $.scojs_message('保存成功', $.scojs_message.TYPE_OK);
                $('#saveBtn').attr('disabled', false);
                
                //异步刷新明细表
                waveOrder.refleshTable(order.ID);
            }else{
                $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
                $('#saveBtn').attr('disabled', false);
            }
        },'json').fail(function() {
            $.scojs_message('保存失败', $.scojs_message.TYPE_ERROR);
            $('#saveBtn').attr('disabled', false);
          });
    });  
} );