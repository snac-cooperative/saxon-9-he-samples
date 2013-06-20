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
            <xsl:text>&#x0A;shell: </xsl:text>
            <xsl:value-of select="eg:shell('date', '-d10 jul 2013')"/>
            <xsl:text>&#x0A;</xsl:text>
            <!--
                When creating command line args for the shell, do not use double quotes. Since each arg is a
                separate string, embedded whitespace is not seen as argument separator happens in a 'real'
                shell.

                Wrong: -d&quot;2 days ago&quot;
                Right: -d2 days ago
            -->
            <xsl:value-of select="eg:shell('date', '-d2 days ago')"/>
            <xsl:text>&#x0A;</xsl:text>
            <xsl:value-of select="eg:shell('date','--date=last month', '--rfc-3339=seconds')"/>
            <xsl:text>&#x0A;</xsl:text>
            <xsl:variable name="date_from_shell">
                <xsl:value-of select="eg:shell('date','--date=last month', '+%Y-%m-%dT%H:%M:%S')"/>
            </xsl:variable>
            <xsl:text>formatted date from shell:</xsl:text>
            <xsl:value-of select="format-dateTime($date_from_shell, '[Y0001]-[M01]-[D01] [h01]:[m01]:[s01]')"/>
            <xsl:text>&#x0A;</xsl:text>
        </output>
    </xsl:template>
</xsl:stylesheet>
