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
        
		
                $('.saveButton').click(function (){
                    $('#form1').submit();
                });
		
	
            });
        </script>
        <style type="text/css">
            .jbxx td {
                padding: 5px;
            }
            .jbxx td span {
                float: right;
            }

        </style>
    </head>
    <body>

        <div id="messages" style="color: red " ></div>
        <form id="form1" action="${ctx}/attach/uploadAttachment.widgets" method="post" enctype ="multipart/form-data">

            <div class="tableGroup otherInfo">
                <div>
                    <table style="width: 100%">
                        <tr  style="width: 100%">
                            <td ><input type="file" id ="attachment" name="attachment" style="width:300px" />
                             <input type="text" id="handler" name="handler" value="DefualtattAchmentUploadHandler" style="display: none" />
                             <input type="text" id="serviceid" name="serviceid" value="2990200433333" style="display: none" />
                             <input type="text" id="attachinfo" name="attachinfo" value="/uploadDir" style="display: none" />
                            </td>
                        </tr>   
                    </table>
                </div>

            </div>

        </form>
          <input type="button" class="submitLong saveButton" value="上传" />
        <div class="formBtn" align="center">
            <input type="button" class="submitLong saveButton" value="保存" style="display: none" /> 
            <input type="button" class="submitLong cancelButton" value="取消" style="display: none" />
        </div>

    </body>
</html>