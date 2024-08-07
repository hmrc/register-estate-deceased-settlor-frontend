/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package views

import base.SpecBase
import models.UserAnswers
import org.jsoup.Jsoup
import org.jsoup.nodes.{Document, Element}
import org.scalatest.Assertion
import play.twirl.api.Html

import scala.reflect.ClassTag

trait ViewSpecBase extends SpecBase {

  def viewFor[A](data: Option[UserAnswers] = None)(implicit tag: ClassTag[A]): A = {
    val application = applicationBuilder(data).build()
    val view = application.injector.instanceOf[A]
    application.stop()
    view
  }

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String, args: Any*): Assertion =
    assertEqualsValue(doc, cssSelector, ViewUtils.breadcrumbTitle(messages(expectedMessageKey, args: _*)))

  def assertDoesNotContainText(doc: Document, text: String): Assertion =
    assert(!doc.toString.contains(text), "\n\ntext " + text + " was rendered on the page.\n")

  def assertEqualsValue(doc: Document, cssSelector: String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if (elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    //<p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def assertPageTitleWithCaptionEqualsMessages(doc: Document,
                                               expectedCaptionMessageKey: String,
                                               captionParam: String,
                                               expectedMessageKey: String,
                                               messageKeyParam: String = ""): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1
    val actual = headers.first.text.replaceAll("\u00a0", " ")

    val expectedCaption = messages(expectedCaptionMessageKey, captionParam).replaceAll("&nbsp;", " ")
    val expectedHeading = messages(expectedMessageKey, messageKeyParam).replaceAll("&nbsp;", " ")

    actual mustBe s"$expectedCaption $expectedHeading"
  }

  def assertPageTitleEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")

    val expected = messages(expectedMessageKey, args: _*).replaceAll("&nbsp;", " ")

    headers.size mustBe 1
    headers.first.text.replaceAll("\u00a0", " ") mustBe expected
  }

  def assertPageTitleWithCaptionEqualsMessage(doc: Document,
                                              expectedMessageKey: String,
                                              captionParam: String,
                                              args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1

    val expectedCaption = messages(s"$expectedMessageKey.caption", captionParam)

    val expectedHeading = messages(s"$expectedMessageKey.heading", args: _*)

    val expected = s"$expectedCaption $expectedHeading"
      .replaceAll("&nbsp;", " ")

    val actual = headers
      .first
      .text
      .replaceAll("\u00a0", " ")

    actual mustBe expected
  }

  def assertPageTitleWithSectionSubheading(doc: Document,
                                           expectedMessageKey: String,
                                           captionParam: String,
                                           args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1

    val expectedCaption = s"${messages(s"$expectedMessageKey.caption.hidden")} ${messages(s"$expectedMessageKey.caption", captionParam)}"

    val expectedHeading = messages(s"$expectedMessageKey.heading", args: _*)

    val expected = s"$expectedCaption $expectedHeading"
      .replaceAll("&nbsp;", " ")

    val actual = headers
      .first
      .text
      .replaceAll("\u00a0", " ")

    actual mustBe expected
  }

  def assertContainsText(doc: Document, text: String): Assertion = assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def assertContainsMessages(doc: Document, expectedMessageKeys: String*): Unit = {
    for (key <- expectedMessageKeys) assertContainsText(doc, messages(key))
  }

  def assertAttributeValueForElement(element: Element, attribute: String, attributeValue: String): Assertion = {
    assert(element.attr(attribute) == attributeValue)
  }

  def assertElementHasAttribute(element: Element, attribute: String): Assertion = {
    assert(element.hasAttr(attribute))
  }

  def assertElementDoesNotHaveAttribute(element: Element, attribute: String): Assertion = {
    assert(!element.hasAttr(attribute))
  }

  def assertRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) != null, "\n\nElement " + id + " was not rendered on the page.\n")
  }

  def assertNotRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) == null, "\n\nElement " + id + " was rendered on the page.\n")
  }

  def assertContainsTextForId(doc: Document, id: String, expectedText: String): Assertion = {
    assert(doc.getElementById(id).text() == expectedText, s"\n\nElement $id does not have text $expectedText")
  }

  def assertRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(!doc.select(cssSelector).isEmpty, "Element " + cssSelector + " was not rendered on the page.")
  }

  def assertNotRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(doc.select(cssSelector).isEmpty, "\n\nElement " + cssSelector + " was rendered on the page.\n")
  }

  def assertContainsLabel(doc: Document, forElement: String, expectedText: String, expectedHintText: Option[String] = None): Any = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    val label = labels.first
    assert(label.text().contains(expectedText), s"\n\nLabel for $forElement was not $expectedText")

    assertContainsHint(doc, forElement, expectedHintText)
  }

  def assertContainsHint(doc: Document, forElement: String, expectedHintText: Option[String]): Any = {
    if (expectedHintText.isDefined) {
      assert(doc.getElementsByClass("govuk-hint").first.text == expectedHintText.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintText")
    }
  }

  def assertContainsClass(doc: Document, className: String): Any = {
    assert(doc.getElementsByClass(className).size() > 0, s"\n\nPage did not contain element with class $className")
  }

  def assertElementHasClass(doc: Document, id: String, expectedClass: String): Assertion = {
    assert(doc.getElementById(id).hasClass(expectedClass), s"\n\nElement $id does not have class $expectedClass")
  }

  def assertRenderedByClass(doc: Document, className: String): Assertion =
    assert(!doc.getElementsByClass(className).isEmpty, "\n\nElement " + className + " was not rendered on the page.\n")

  def assertNotRenderedByClass(doc: Document, className: String): Assertion = {
    assert(doc.getElementsByClass(className).isEmpty, "\n\nElement " + className + " was rendered on the page.\n")
  }

  def assertContainsRadioButton(doc: Document, id: String, name: String, value: String, isChecked: Boolean): Assertion = {
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!radio.hasAttr("checked"), s"\n\nElement $id is checked")
    }
  }
}
