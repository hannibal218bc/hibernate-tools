/*
 * Created on 2004-12-01
 *
 */
package org.hibernate.tool.hbm2x.PropertiesTest;

import java.io.File;
import java.nio.file.Files;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2x.ArtifactCollector;
import org.hibernate.tool.hbm2x.Exporter;
import org.hibernate.tool.hbm2x.HibernateMappingExporter;
import org.hibernate.tool.hbm2x.POJOExporter;
import org.hibernate.tools.test.util.FileUtil;
import org.hibernate.tools.test.util.HibernateUtil;
import org.hibernate.tools.test.util.JUnitUtil;
import org.hibernate.tools.test.util.JavaUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 * @author Josh Moore josh.moore@gmx.de
 * @author koen
 */
public class TestCase {
	
	private static final String[] HBM_XML_FILES = new String[] {
			"Properties.hbm.xml"
	};
	
	@Rule
	public TemporaryFolder temporaryFolder = new TemporaryFolder();

	private ArtifactCollector artifactCollector;
	private File exporterOutputDir;
	
	@Before
	public void setUp() throws Exception {
		Configuration configuration = 
				HibernateUtil.initializeConfiguration(this, HBM_XML_FILES);
		artifactCollector = new ArtifactCollector();
		exporterOutputDir = new File(temporaryFolder.getRoot(), "exporterOutput");
		exporterOutputDir.mkdir();
		Exporter exporter = new POJOExporter( configuration, exporterOutputDir);
		exporter.setArtifactCollector(artifactCollector);
		Exporter hbmexporter = new HibernateMappingExporter(configuration, exporterOutputDir);
		hbmexporter.setArtifactCollector(artifactCollector);
		exporter.start();
		hbmexporter.start();
	}	
	
	@Test
	public void testNoGenerationOfEmbeddedPropertiesComponent() {
		Assert.assertEquals(2, artifactCollector.getFileCount("java"));
		Assert.assertEquals(2, artifactCollector.getFileCount("hbm.xml"));
	}
	
	@Test
	public void testGenerationOfEmbeddedProperties() {
		File outputXml = new File(exporterOutputDir,  "properties/PPerson.hbm.xml");
		JUnitUtil.assertIsNonEmptyFile(outputXml);
    	SAXReader xmlReader = new SAXReader();
    	xmlReader.setValidation(true);
		Document document;
		try {
			document = xmlReader.read(outputXml);
			XPath xpath = DocumentHelper.createXPath("//hibernate-mapping/class/properties");
			List<?> list = xpath.selectNodes(document);
			Assert.assertEquals("Expected to get one properties element", 1, list.size());
			Element node = (Element) list.get(0);
			Assert.assertEquals(node.attribute( "name" ).getText(),"emergencyContact");
			Assert.assertNotNull(
					FileUtil.findFirstString(
							"name", 
							new File(exporterOutputDir, "properties/PPerson.java" )));
			Assert.assertNull(
					"Embedded component/properties should not show up in .java", 
					FileUtil.findFirstString(
							"emergencyContact", 
							new File(exporterOutputDir, "properties/PPerson.java" )));		
		} catch (DocumentException e) {
			Assert.fail("Can't parse file " + outputXml.getAbsolutePath());
		}		
	}
	
	@Test
	public void testCompilable() throws Exception {
		String propertiesUsageResourcePath = "/org/hibernate/tool/hbm2x/PropertiesTest/PropertiesUsage.java_";
		File propertiesUsageOrigin = new File(getClass().getResource(propertiesUsageResourcePath).toURI());
		File propertiesUsageDestination = new File(exporterOutputDir, "properties/PropertiesUsage.java");
		File targetDir = new File(temporaryFolder.getRoot(), "compilerOutput" );
		targetDir.mkdir();	
		Files.copy(propertiesUsageOrigin.toPath(), propertiesUsageDestination.toPath());
		JavaUtil.compile(exporterOutputDir, targetDir);
		Assert.assertTrue(new File(targetDir, "properties/PCompany.class").exists());
		Assert.assertTrue(new File(targetDir, "properties/PPerson.class").exists());
		Assert.assertTrue(new File(targetDir, "properties/PropertiesUsage.class").exists());
	}

}
