package cinema.config

import java.io.File

import org.scalatest.{BeforeAndAfterAll, FlatSpec}

class LoadableConfigGitTest extends FlatSpec with LoadableConfig with BeforeAndAfterAll {

  def getLocalPath: String = {
    val localList = List("target", "temp")
    new File(".").getCanonicalPath + localList.fold("")(_ + File.separator +  _)
  }

  override def beforeAll(): Unit = {
    if (new File(getLocalPath).exists()) {
      new File(getLocalPath).delete()
    }
  }

  "Git config" should "be loaded" in {
      val local = Some(getLocalPath)
      val pathList = List("src", "test", "resources", "git")
      val path = new File(".").getCanonicalPath + pathList.fold("")(_ + File.separator +  _)
      val gitPath = "file://" + path
      assert(load(Some(gitPath), local).getString("test.data") == "TEST3")
    }

  override def afterAll(): Unit = new File(getLocalPath) delete()
}