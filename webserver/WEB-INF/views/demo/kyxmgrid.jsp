<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ include file="/common/taglibs.jsp"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        
        <link rel="stylesheet" href="${ctx}/resources/js/jquery-ui-1.8.15/themes/base/jquery.ui.all.css">
	<script src="${ctx}/resources/js/jquery-ui-1.8.15/jquery-1.6.2.js"></script>
	<script src="${ctx}/resources/js/jquery-ui-1.8.15/ui/jquery.ui.core.js"></script>
	<script src="${ctx}/resources/js/jquery-ui-1.8.15/ui/jquery.ui.widget.js"></script>
	<script src="${ctx}/resources/js/jquery-ui-1.8.15/ui/jquery.ui.position.js"></script>
	<script src="${ctx}/resources/js/jquery-ui-1.8.15/ui/jquery.ui.autocomplete.js"></script>
	<link rel="stylesheet" href="${ctx}/resources/js/jquery-ui-1.8.15/demos/demos.css">
        
        <!-- 测试页面 -->
        <title>科研项目</title>
        <script type="text/javascript">  
        
               
         $(function() {
		$( "#xmmc" )
			.bind( "keydown", function( event ) {
				if ( event.keyCode === $.ui.keyCode.TAB &&
						$( this ).data( "autocomplete" ).menu.active ) {
					event.preventDefault();
				}
			})
			.autocomplete({
				minLength: 0,
				source: function( request, response ) {
                                        $.ajax({
					url: "http://ws.geonames.org/searchJSON",
					dataType: "jsonp",
					data: {
						featureClass: "P",
						style: "full",
						maxRows: 20,
						name_startsWith: request.term
					},
					success: function(data) {
							response( $.map( data.geonames, function( item ) {
							return {
								label: item.name + (item.adminName1 ? ", " + item.adminName1 : "") + ", " + item.countryName,
								value: item.name
							}
                                                    }));
                                                }
                                             });
                                        
                                        
				},
				focus: function() {
					return false;
				},
				select: function( event, ui ) {
					var terms = split( this.value );
					this.value = terms.join( ", " );
					return false;
				},
                               	open: function() {
				$( this ).removeClass( "ui-corner-all" ).addClass( "ui-corner-top" );
                            },
                            close: function() {
				$( this ).removeClass( "ui-corner-top" ).addClass( "ui-corner-all" );
			} 
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

        <form id="form1" action="${ctx}/demo/kyxmsave.do" method="post">

            <input type="hidden" name="bizobjs" value="xmxx:T_XM_JBXX"/>
            <div>
                <table class="table_con" width="80%" border="0"  cellpadding="0.5" cellspacing="0">
                     <tr>
                         <td>项目名称</td><td><input  class="" autofocus="true" id="xmmc" type="text" width="15%"/>
                             <button class="" value="查询"  style=" width: 100px"/> 项目查询</td>
                      </tr>
                </table>
            </div>
            
                <div>
                    <div class="formBtn">
                        <a href="${ctx}/demo/kyxmform.do" class="submitLongLong">添加项目</a>
                    </div>
                        <table class="table_con" width="100%" border="0"  cellpadding="0" cellspacing="0">
                        <tr class="tableTr">
                            <th width="15%">项目名称</th>
                            <th width="15%">项目级别</th>
                            <th width="8%">项目类别</th>
                            <th width="8%">项目二级类别</th>
                            <th width="5%">立项日期</th>
                             <th width="5%">开始日期</th>
                            <th width="5%">结束日期</th>
                        </tr>
                        <c:forEach items="${xmxx}" var="xmxx">
                            <tr class="t_con">
                                <td><input name="xmxx.xmmc" value="${xmxx.xmmc}" baseType="text"/></td>
                                <td><input name="xmxx.xmjb" class="{required:true}" value="${xmxx.xmjb}"  baseType="xmjb"/></td>
                                <td><input name="xmxx.xmlb" class="{required:true}" value="${xmxx.xmlb}"  baseType="text"/></td>
                                <td><input name="xmxx.xmejlb" class="{required:true}" value="${xmxx.xmejlb}"  baseType="text"/></td>
                                <td><input name="xmxx.lxrq" class="{required:true}" value="${xmxx.lxrq}"  baseType="date"/></td>
                                <td><input name="xmxx.ksrq" class="{required:true}" value="${xmxx.ksrq}" baseType="date"/></td>
                                <td><input name="xmxx.jsrq" class="{required:true}" value="${xmxx.jsrq}" baseType="date"/></td>
                            </tr>
                        </c:forEach>
                    </table>
                </div>
         
    </body>
</html>