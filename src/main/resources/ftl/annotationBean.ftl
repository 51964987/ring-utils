package ${package};

<#assign impo=""/>
<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
			<#if types["${key}"]["class"]??>
			<#if impo?index_of(";${types['${key}']['class']};") == -1>
import ${types["${key}"]["class"]};
			<#assign impo="${impo};${types['${key}']['class']};"/>
			</#if>
			</#if>
			</#if>
		</#list>
	</#items>
</#list>

import javax.persistence.Entity;
import javax.persistence.Table;
<#list cols>
	<#items as col>
		<#list pkeys as pkcol>
			<#if pkcol.name=="${col.name}">
				<#if impo?index_of(";javax.persistence.Id;") == -1>
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
					<#assign impo="${impo};javax.persistence.Id;};"/>
				</#if>		
			</#if>
				</#list>
	</#items>
</#list>

/**
 * 
 * @author ring
 * @date ${.now}
 * @version V1.0
 
 */
@Entity
@Table(name = "${tableName}")
public class ${clsName}  implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	
<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
				<#list pkeys as pkcol>
					<#if pkcol.name=="${col.name}">
	@Id	
	@GeneratedValue(strategy = GenerationType.<#if col.IS_AUTOINCREMENT="YES">AUTO<#else>IDENTITY</#if>)
					</#if>
				</#list>
	private ${types["${key}"].type} ${col.name};		//${col.REMARKS}
			</#if>
		</#list>
	</#items>
</#list>

<#list cols>
	<#items as col>
		<#assign typename="${col.TYPE_NAME?lower_case}"/>
		<#list types?keys as key>
			<#if key="${typename}">
	public ${types["${key}"].type} get${col.name?cap_first}(){
		return ${col.name};
	}
	public void set${col.name?cap_first}(${types["${key}"].type} ${col.name}){
		this.${col.name}=${col.name};
	}
			</#if>
		</#list>
	</#items>
</#list>

}