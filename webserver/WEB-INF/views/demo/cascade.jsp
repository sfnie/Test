<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <!-- 测试页面 -->
        <title>级联演示</title>
        
        <style type="text/css">
            #table1 td {
                padding: 5px;
            }

            #table1 td span {
                float: right;
            }
        </style>


        <style type="text/css">
            .buttons a,.buttons button {
                display: block;
                float: left;
                margin: 0 7px 0 0;
                background-color: #f5f5f5;
                border: 1px solid #dedede;
                border-top: 1px solid #eee;
                border-left: 1px solid #eee;
                font-family: "Lucida Grande", Tahoma, Arial, Verdana, sans-serif;
                font-size: 100%;
                line-height: 130%;
                text-decoration: none;
                font-weight: bold;
                color: #565656;
                cursor: pointer;
                padding: 5px 10px 6px 7px; /* Links */
            }

            .buttons button {
                width: auto;
                overflow: visible;
                padding: 4px 10px 3px 7px; /* IE6 */
            }

            .buttons button[type] {
                padding: 5px 10px 5px 7px; /* Firefox */
                line-height: 17px; /* Safari */
            }

            *:first-child+html button[type] {
                padding: 4px 10px 3px 7px; /* IE7 */
            }

            .buttons button img,.buttons a img {
                margin: 0 3px -3px 0 !important;
                padding: 0;
                border: none;
                width: 16px;
                height: 16px;
            }
        </style>

        <script type="text/javascript">  
            $(function() {

                var form1 = $('#form1').editableForm();
                form1.editableForm('showEdit');
                $('#select_ydxl').combobox('addCascade','select_yddl','yddl');
		
                form1.validate({submitHandler: function() { 
                        $('#form1')[0].submit();
                    }});
        
            });
        </script>
    </head>
    <body>

        <div id="messages" style="color: red " ></div>

        <div align="center" style="margin-top: 21px;">
            <h2>学籍变动申请</h2>
        </div>

        <!-- 页面内容开始 -->
        <div align="center" style="margin-top: 7px;">
            <div>
                <form id="form1" action="${ctx}/xjxx/xjyd/create" method="post">

                    <div style="color: red;">

                        请先选择学籍变动大类和学籍变动类型，点击下方的“学籍变动申请”按钮，进入学籍变动申请页面</div>
                    <div>
                        <br />
                        <table style="" id="table1">
                            <tr>
                                <td>异动大类</td>
                                <td>
                                    <input id="select_yddl" baseType="yddl"/>
                                </td>
                                <td>异动类别</td>
                                <td>
                                    <input baseType="ydxl" name="ydlbm" id="select_ydxl"/>
                                </td>
                            </tr>
                            <tr>
                                <td colspan="4" style="text-align: center"><span
                                        class="buttons"><a href="#" id="btnBdsq">学籍变动申请</a></span></td>
                            </tr>
                        </table>
                        <br />
                    </div>
                    <div>
                        <table>
                            <tr>
                                <td>注：</td>
                                <td>1):不能同时提出多个学籍变动，只有研究生院对当前变动申请“审核通过”后，方可提出下一次学籍变动申请;  </td>
                            </tr>
                            <tr>
                                <td></td>
                                <td>2):审核状态为“院系退回”的申请，如需进行申请，请到“学籍变动申请”对原申请修改后，继续提出申请。</td>
                            </tr>
                        </table>
                    </div>
                </form>
            </div>
        </div>

    </body>
</html>