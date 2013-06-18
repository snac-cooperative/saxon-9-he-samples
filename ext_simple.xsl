<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:eg="http://example.com/saxon-extension"
                exclude-result-prefixes="xs">
    
    <!--
        The namespace-uri (of eg) must match the QName from ExtensionFunction.getName() as exists in
        ext_simple.java.
        
        javac ext_simple.java
        java ext_simple
    -->

    <xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="dateNow" select="current-dateTime()"/>
    
    <xsl:template match="/">
        <xsl:text>format-dateTime: </xsl:text>
        <xsl:value-of select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01] [h01]:[m01]:[s01]')"/>
        <xsl:text>&#x0A;date: </xsl:text>
        <xsl:value-of select="$dateNow"/>
        <xsl:text>&#x0A;sqrt: </xsl:text>
        <xsl:value-of select="eg:sqrt(49)"/>
        <xsl:text>&#x0A;</xsl:text>
    </xsl:template>
</xsl:stylesheet>
