package dev.ferrite.jetbrains

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Unit tests for the FerriteQL live template definitions.
 *
 * Parses the live template XML files shipped in resources/liveTemplates/ and
 * verifies their structure: template names, variable definitions, descriptions,
 * and that every template has a context configured.  These tests run against
 * the XML files directly -- no IntelliJ template engine required.
 */
class LiveTemplateTest {

    private val resourceBase = File("src/main/resources/liveTemplates")

    private fun parseTemplateFile(filename: String): List<TemplateInfo> {
        val file = File(resourceBase, filename)
        if (!file.exists()) {
            // In CI the working directory may differ; try absolute
            val altFile = File(
                "/Users/josedab/Code/FerriteLabs/jetbrains-ferrite/src/main/resources/liveTemplates/$filename"
            )
            if (!altFile.exists()) return emptyList()
            return parseTemplateXml(altFile)
        }
        return parseTemplateXml(file)
    }

    private fun parseTemplateXml(file: File): List<TemplateInfo> {
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(file)
        doc.documentElement.normalize()

        val templates = mutableListOf<TemplateInfo>()
        val templateNodes: NodeList = doc.getElementsByTagName("template")

        for (i in 0 until templateNodes.length) {
            val elem = templateNodes.item(i) as Element
            val name = elem.getAttribute("name")
            val value = elem.getAttribute("value")
            val description = elem.getAttribute("description")
            val toReformat = elem.getAttribute("toReformat")

            val variables = mutableListOf<VariableInfo>()
            val varNodes = elem.getElementsByTagName("variable")
            for (j in 0 until varNodes.length) {
                val varElem = varNodes.item(j) as Element
                variables.add(
                    VariableInfo(
                        name = varElem.getAttribute("name"),
                        expression = varElem.getAttribute("expression"),
                        defaultValue = varElem.getAttribute("defaultValue"),
                        alwaysStopAt = varElem.getAttribute("alwaysStopAt").toBooleanStrictOrNull() ?: false
                    )
                )
            }

            val hasContext = elem.getElementsByTagName("context").length > 0
            templates.add(TemplateInfo(name, value, description, toReformat, variables, hasContext))
        }
        return templates
    }

    data class TemplateInfo(
        val name: String,
        val value: String,
        val description: String,
        val toReformat: String,
        val variables: List<VariableInfo>,
        val hasContext: Boolean
    )

    data class VariableInfo(
        val name: String,
        val expression: String,
        val defaultValue: String,
        val alwaysStopAt: Boolean
    )

    // =======================================================================
    // FerriteQL.xml template tests
    // =======================================================================

    @Test
    fun `FerriteQL template file exists and is parseable`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        assertTrue("Should have at least one template", templates.isNotEmpty())
    }

    @Test
    fun `FerriteQL template names are unique`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val names = templates.map { it.name }
        assertEquals("Template names must be unique", names.size, names.toSet().size)
    }

    @Test
    fun `Every FerriteQL template has a description`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            assertTrue("Template '${t.name}' should have a description", t.description.isNotBlank())
        }
    }

    @Test
    fun `Every FerriteQL template has a context block`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            assertTrue("Template '${t.name}' should have a context", t.hasContext)
        }
    }

    @Test
    fun `Every FerriteQL template has a non-empty value`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            assertTrue("Template '${t.name}' should have a non-empty value", t.value.isNotBlank())
        }
    }

    @Test
    fun `FerriteQL template variables have alwaysStopAt true`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            for (v in t.variables) {
                assertTrue(
                    "Variable '${v.name}' in template '${t.name}' should have alwaysStopAt=true",
                    v.alwaysStopAt
                )
            }
        }
    }

    @Test
    fun `FerriteQL template variables have default values`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            for (v in t.variables) {
                assertTrue(
                    "Variable '${v.name}' in template '${t.name}' should have a default value",
                    v.defaultValue.isNotEmpty()
                )
            }
        }
    }

    @Test
    fun `FerriteQL template variable references match dollar-sign placeholders`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            val variableNames = t.variables.map { it.name }.toSet()
            // Find all $VAR$ references in the template value
            val pattern = Regex("\\$([A-Za-z_][A-Za-z0-9_]*)\\$")
            val referencedVars = pattern.findAll(t.value).map { it.groupValues[1] }.toSet()

            for (ref in referencedVars) {
                assertTrue(
                    "Template '${t.name}' references \$$ref\$ but no variable definition found",
                    variableNames.contains(ref)
                )
            }

            for (varName in variableNames) {
                assertTrue(
                    "Template '${t.name}' defines variable '$varName' but never references it",
                    referencedVars.contains(varName)
                )
            }
        }
    }

    @Test
    fun `get template exists with correct structure`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val get = templates.find { it.name == "get" }
        assertNotNull("'get' template should exist", get)
        assertTrue(get!!.value.contains("GET"))
        assertEquals(1, get.variables.size)
        assertEquals("KEY", get.variables[0].name)
    }

    @Test
    fun `set template exists with correct structure`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val set = templates.find { it.name == "set" }
        assertNotNull("'set' template should exist", set)
        assertTrue(set!!.value.contains("SET"))
        assertEquals(2, set.variables.size)
        val varNames = set.variables.map { it.name }.toSet()
        assertTrue(varNames.contains("KEY"))
        assertTrue(varNames.contains("VALUE"))
    }

    @Test
    fun `setex template includes EX option`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val setex = templates.find { it.name == "setex" }
        assertNotNull("'setex' template should exist", setex)
        assertTrue(setex!!.value.contains("EX"))
        assertTrue(setex.variables.any { it.name == "SECONDS" })
    }

    @Test
    fun `hset template has field and value variables`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val hset = templates.find { it.name == "hset" }
        assertNotNull("'hset' template should exist", hset)
        val varNames = hset!!.variables.map { it.name }.toSet()
        assertTrue(varNames.contains("KEY"))
        assertTrue(varNames.contains("FIELD"))
        assertTrue(varNames.contains("VALUE"))
    }

    @Test
    fun `hgetall template exists`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val hgetall = templates.find { it.name == "hgetall" }
        assertNotNull("'hgetall' template should exist", hgetall)
        assertTrue(hgetall!!.value.contains("HGETALL"))
    }

    @Test
    fun `lpush template exists`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val lpush = templates.find { it.name == "lpush" }
        assertNotNull("'lpush' template should exist", lpush)
        assertTrue(lpush!!.value.contains("LPUSH"))
    }

    @Test
    fun `lrange template has start and stop variables`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val lrange = templates.find { it.name == "lrange" }
        assertNotNull("'lrange' template should exist", lrange)
        val varNames = lrange!!.variables.map { it.name }.toSet()
        assertTrue(varNames.contains("START"))
        assertTrue(varNames.contains("STOP"))
    }

    @Test
    fun `zadd template has score and member variables`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val zadd = templates.find { it.name == "zadd" }
        assertNotNull("'zadd' template should exist", zadd)
        val varNames = zadd!!.variables.map { it.name }.toSet()
        assertTrue(varNames.contains("SCORE"))
        assertTrue(varNames.contains("MEMBER"))
    }

    @Test
    fun `zrange template includes WITHSCORES`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val zrange = templates.find { it.name == "zrange" }
        assertNotNull("'zrange' template should exist", zrange)
        assertTrue(zrange!!.value.contains("WITHSCORES"))
    }

    @Test
    fun `xadd template exists`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val xadd = templates.find { it.name == "xadd" }
        assertNotNull("'xadd' template should exist", xadd)
        assertTrue(xadd!!.value.contains("XADD"))
    }

    @Test
    fun `vectorcreate template includes HNSW and DIM`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val vc = templates.find { it.name == "vectorcreate" }
        assertNotNull("'vectorcreate' template should exist", vc)
        assertTrue(vc!!.value.contains("VECTOR.CREATE"))
        assertTrue(vc.value.contains("HNSW"))
        assertTrue(vc.value.contains("DIM"))
        assertTrue(vc.value.contains("DISTANCE"))
        assertTrue(vc.value.contains("COSINE"))
    }

    @Test
    fun `vectorsearch template includes TOP_K`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val vs = templates.find { it.name == "vectorsearch" }
        assertNotNull("'vectorsearch' template should exist", vs)
        assertTrue(vs!!.value.contains("VECTOR.SEARCH"))
        assertTrue(vs.value.contains("TOP_K"))
    }

    @Test
    fun `tsadd template exists with time series command`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val ts = templates.find { it.name == "tsadd" }
        assertNotNull("'tsadd' template should exist", ts)
        assertTrue(ts!!.value.contains("TS.ADD"))
    }

    @Test
    fun `tsrange template includes AGGREGATION`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val ts = templates.find { it.name == "tsrange" }
        assertNotNull("'tsrange' template should exist", ts)
        assertTrue(ts!!.value.contains("TS.RANGE"))
        assertTrue(ts.value.contains("AGGREGATION"))
    }

    @Test
    fun `semanticset template exists`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val sem = templates.find { it.name == "semanticset" }
        assertNotNull("'semanticset' template should exist", sem)
        assertTrue(sem!!.value.contains("SEMANTIC.SET"))
    }

    @Test
    fun `semanticsearch template includes THRESHOLD`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        val sem = templates.find { it.name == "semanticsearch" }
        assertNotNull("'semanticsearch' template should exist", sem)
        assertTrue(sem!!.value.contains("SEMANTIC.SEARCH"))
        assertTrue(sem.value.contains("THRESHOLD"))
    }

    @Test
    fun `toReformat is false for all FerriteQL templates`() {
        val templates = parseTemplateFile("FerriteQL.xml")
        for (t in templates) {
            assertEquals(
                "Template '${t.name}' should have toReformat=false",
                "false",
                t.toReformat
            )
        }
    }

    // =======================================================================
    // Other language template files exist
    // =======================================================================

    @Test
    fun `TypeScript template file exists and is parseable`() {
        val templates = parseTemplateFile("FerriteTypeScript.xml")
        assertTrue("TypeScript templates should exist", templates.isNotEmpty())
    }

    @Test
    fun `Python template file exists and is parseable`() {
        val templates = parseTemplateFile("FerritePython.xml")
        assertTrue("Python templates should exist", templates.isNotEmpty())
    }

    @Test
    fun `Rust template file exists and is parseable`() {
        val templates = parseTemplateFile("FerriteRust.xml")
        assertTrue("Rust templates should exist", templates.isNotEmpty())
    }

    @Test
    fun `Java template file exists and is parseable`() {
        val templates = parseTemplateFile("FerriteJava.xml")
        assertTrue("Java templates should exist", templates.isNotEmpty())
    }

    @Test
    fun `Go template file exists and is parseable`() {
        val templates = parseTemplateFile("FerriteGo.xml")
        assertTrue("Go templates should exist", templates.isNotEmpty())
    }

    @Test
    fun `All template files have unique names within their group`() {
        val files = listOf(
            "FerriteQL.xml",
            "FerriteTypeScript.xml",
            "FerritePython.xml",
            "FerriteRust.xml",
            "FerriteJava.xml",
            "FerriteGo.xml"
        )
        for (filename in files) {
            val templates = parseTemplateFile(filename)
            val names = templates.map { it.name }
            assertEquals(
                "Template names in $filename must be unique",
                names.size,
                names.toSet().size
            )
        }
    }

    @Test
    fun `All template files have descriptions for every template`() {
        val files = listOf(
            "FerriteQL.xml",
            "FerriteTypeScript.xml",
            "FerritePython.xml",
            "FerriteRust.xml",
            "FerriteJava.xml",
            "FerriteGo.xml"
        )
        for (filename in files) {
            val templates = parseTemplateFile(filename)
            for (t in templates) {
                assertTrue(
                    "Template '${t.name}' in $filename should have a description",
                    t.description.isNotBlank()
                )
            }
        }
    }

    @Test
    fun `All template files have context blocks for every template`() {
        val files = listOf(
            "FerriteQL.xml",
            "FerriteTypeScript.xml",
            "FerritePython.xml",
            "FerriteRust.xml",
            "FerriteJava.xml",
            "FerriteGo.xml"
        )
        for (filename in files) {
            val templates = parseTemplateFile(filename)
            for (t in templates) {
                assertTrue(
                    "Template '${t.name}' in $filename should have a context",
                    t.hasContext
                )
            }
        }
    }
}
