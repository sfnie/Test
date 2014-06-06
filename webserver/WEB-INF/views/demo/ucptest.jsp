<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- 测试页面 -->
        <title>attachment</title>
        <script type="text/javascript">  
            $(function() {

                var form1 = $('#form1').editableForm();
        		
                form1.validate({submitHandler: function() { 
                        $('#form1')[0].submit();
                }});
        
                $('.editButton').click(function (){
                    form1.editableForm('showEdit');
                    $('.editButton').hide();
                    $('.printButton').hide();
                    $('.cancelButton').show();
                    $('.saveButton').show();
		
                });
		
                $('.saveButton').click(function (){
                    $('#form1').submit();
                });
		
                $('.cancelButton').click(function (){
                    form1.editableForm('showView');
                    $('.cancelButton').hide();
                    $('.saveButton').hide();
                    $('.editButton').show();
                    $('.printButton').show();
                });
		
	
            });
        </script>
    </head>
    <body>
	
        <form id="form1" action="${ctx}/demo/ucpTest.do" method="post" >

            <div class="tableGroup otherInfo">
                <div>
                    <table style="width: 100%">
                        <tr  style="width: 100%">
                            <td >接收人工号    :  
                            	<input type="text" id ="userId" name="userId" value="01004172" style="width:300px" />
                            	<input type="text" id ="apiKey" name="apiKey" value="62C11DABDCD5E4F317A9088F6056D796" style="display:none" />
                            	<input type="text" id ="method" name="method" value="createNewMessage" style="display:none" />
                            	<input type="text" id ="returnType" name="returnType" value="xml" style="display:none" />
                            	<input type="text" id ="subject" name="subject" value="UCP发送测试" style="display:none" />
                            </td>
                        </tr>   
                        	
                        
                        <tr>
                        	<td>
                        		发送内容     ： <input type="text" id ="content" name="content" value="" style="width:300px" />
                        	</td>
                        </tr>
                    </table>
                </div>

            </div>

        </form>
     	   <div class="formBtn" align="center">
            <input type="button" class="submitLong saveButton" value="发送" style="" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="" />
      	  </div>

    </body>
</html>