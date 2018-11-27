package ftl.reports.xml.model

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement

/**
 * Firebase generates testsuites for iOS.
 * .xctestrun file may contain multiple test bundles (each one is a testsuite) */
@JacksonXmlRootElement(localName = "testsuites")
data class JUnitTestResult(
    @JacksonXmlProperty(localName = "testsuite")
    var testsuites: MutableList<JUnitTestSuite>?
) {
    fun mergeTestTimes(other: JUnitTestResult?): JUnitTestResult {
        if (other == null) return this
        if (this.testsuites == null) this.testsuites = mutableListOf()

        // newTestResult.mergeTestTimes(oldTestResult)
        //
        // for each new JUnitTestSuite, check if it exists on old
        // if JUnitTestSuite exists on both then merge test times
        this.testsuites?.forEach { testSuite ->
            val oldSuite = other.testsuites?.firstOrNull { it.name == testSuite.name }
            if (oldSuite != null) testSuite.mergeTestTimes(oldSuite)
        }

        return this
    }

    fun merge(other: JUnitTestResult?): JUnitTestResult {
        if (other == null) return this
        if (this.testsuites == null) this.testsuites = mutableListOf()

        other.testsuites?.forEach { testSuite ->
            val mergeCandidate = this.testsuites?.firstOrNull { it.name == testSuite.name }

            if (mergeCandidate == null) {
                this.testsuites?.add(testSuite)
            } else {
                mergeCandidate.merge(testSuite)
            }
        }

        return this
    }
}
