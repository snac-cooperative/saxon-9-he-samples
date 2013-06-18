<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:eg="http://example.com/saxon-extension"
                exclude-result-prefixes="xs eg">

    <xsl:output method="text" version="1.0" encoding="UTF-8" indent="yes"/>
    
    <xsl:variable name="dateNow" select="current-dateTime()"/>
    
    <xsl:template match="/">
        <output>
            <xsl:text>format-dateTime: </xsl:text>
            <xsl:value-of select="format-dateTime(current-dateTime(), '[Y0001]-[M01]-[D01] [h01]:[m01]:[s01]')"/>
            <xsl:text>&#x0A;date: </xsl:text>
            <xsl:value-of select="$dateNow"/>
            <xsl:text>&#x0A;shift-left: </xsl:text>
            <xsl:value-of select="eg:shift-left(33, 22)"/>
            <xsl:text>&#x0A;</xsl:text>
        </output>
    </xsl:template>
</xsl:stylesheet>
