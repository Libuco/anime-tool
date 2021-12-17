import java.awt.datatransfer.DataFlavor
import java.awt.event._
import java.awt._
import java.awt.geom.Rectangle2D
import java.io.File
import java.lang.reflect.Type
import java.net.URI

import sys.process._
import java.io._

import javax.swing._
import mdlaf.themes.{AbstractMaterialTheme, JMarsDarkTheme, MaterialLiteTheme, MaterialOceanicTheme}
import javax.swing.event._
import java.lang.{Comparable, ProcessBuilder}
import java.util
import java.util.{Collections, Comparator}

import javax.swing.GroupLayout.Alignment
import javax.swing.text.DefaultCaret
import javax.swing.{DefaultListSelectionModel, JLabel}
import jiconfont.icons.font_awesome.FontAwesome
import mdlaf.MaterialLookAndFeel

import scala.collection.mutable.ListBuffer
import jiconfont.swing.IconFontSwing

object Main extends App {

  val stdout = System.out
  val stderr = System.err

  val theme = new MaterialOceanicTheme
  //theme.getBorderList.
  UIManager.setLookAndFeel(new MaterialLookAndFeel(theme))
  IconFontSwing.register(FontAwesome.getIconFont)


  val audioDelayLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.MUSIC, classOf[JButton]),SwingConstants.LEFT)
  audioDelayLabel.setFont(theme.getButtonFont)
  val subDelayLabel = new JLabel("Sub Delay",makeIcon(FontAwesome.CC, classOf[JButton]),SwingConstants.LEFT)
  subDelayLabel.setFont(theme.getButtonFont)

  val clabel1 = new JLabel("")
  clabel1.setFont(theme.getButtonFont)
  val clabel2 = new JLabel("")
  clabel2.setFont(theme.getButtonFont)
  val clabel3 = new JLabel("")
  clabel3.setFont(theme.getButtonFont)





  //val audioDelaySpinnerLabel = new JLabel("Audio Delay",makeIcon(FontAwesome.CLOCK_O, new JButton),SwingConstants.RIGHT)
  val audioDelayText = new JSpinner()
  audioDelayText.setAlignmentX(SwingConstants.RIGHT)
  //audioDelayText.setForeground(theme.getButtonTextColor)
  //audioDelayText.setAlignmentX(SwingConstants.LEFT)
  //audioDelayText.setBorder(BorderFactory.createLineBorder(theme.getButtonTextColor))
  val subDelayText = new JSpinner()
  subDelayText.setAlignmentX(SwingConstants.RIGHT)

  val videoModel = new SortableListModel[ShortFile]
  val audioModel = new SortableListModel[ShortFile]
  val subModel = new SortableListModel[ShortFile]
  val videoList = new JList[ShortFile](videoModel)
  val audioList = new JList[ShortFile](audioModel)
  val subList =  new JList[ShortFile](subModel)

  videoList.setDropMode(DropMode.INSERT)
  videoList.setTransferHandler(ListHandler(videoModel))
  //videoList.setBorder(BorderFactory.createLineBorder(Color.black))
  videoList.setSelectionModel(new ToggleSelectionModel(videoList,audioList,subList))
  //videoList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(audioList, subList))
  videoList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  videoList.addMouseListener(new RightClickMouseAdapter(videoList))

  val videoListScrollPane = new JScrollPane
  videoListScrollPane.add(videoList)
  videoListScrollPane.setViewportView(videoList)

  audioList.setDropMode(DropMode.INSERT)
  audioList.setTransferHandler(ListHandler(audioModel))
  //audioList.setBorder(BorderFactory.createLineBorder(Color.black))
  audioList.setSelectionModel(new ToggleSelectionModel(audioList,videoList, subList))
  //audioList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,subList))
  audioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  audioList.addMouseListener(new RightClickMouseAdapter(audioList))

  val audioListScrollPane = new JScrollPane
  audioListScrollPane.add(audioList)
  audioListScrollPane.setViewportView(audioList)

  subList.setDropMode(DropMode.INSERT)
  subList.setTransferHandler(ListHandler(subModel))
  //subList.setBorder(BorderFactory.createLineBorder(Color.black))
  subList.setSelectionModel(new ToggleSelectionModel(subList,videoList,audioList))
  //subList.getSelectionModel.addListSelectionListener(new SharedListSelectionHandler(videoList,audioList))
  subList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)
  subList.addMouseListener(new RightClickMouseAdapter(subList))

  val subListScrollPane = new JScrollPane
  subListScrollPane.add(subList)
  subListScrollPane.setViewportView(subList)

  val up1btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP,classOf[JButton]))
  up1btn.addActionListener(UpActionListener(videoList))

  val up2btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP, classOf[JButton]))
  up2btn.addActionListener(UpActionListener(audioList))
  val up3btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_UP, classOf[JButton]))
  up3btn.addActionListener(UpActionListener(subList))


  val down1btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down1btn.addActionListener(DownActionListener(videoList))
  val down2btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down2btn.addActionListener(DownActionListener(audioList))
  val down3btn = new JButton(makeIcon(FontAwesome.CHEVRON_CIRCLE_DOWN, classOf[JButton]))
  down3btn.addActionListener(DownActionListener(subList))


  val clear1btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear1btn.addActionListener(ClearActionListener(videoModel))
  val clear2btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear2btn.addActionListener(ClearActionListener(audioModel))
  val clear3btn = new JButton(makeIcon(FontAwesome.FILE, classOf[JButton]))
  clear3btn.addActionListener(ClearActionListener(subModel))

  val del1btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  val del2btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  del1btn.addActionListener(SortActionListener(videoList, videoModel))

  del2btn.addActionListener(SortActionListener(audioList, audioModel))

  val del3btn = new JButton(makeIcon(FontAwesome.SORT_ALPHA_ASC, classOf[JButton]))
  del3btn.addActionListener(SortActionListener(subList, subModel))

  val playButton = new JButton("Play",makeIcon(FontAwesome.PLAY, classOf[JButton]))
  playButton.addActionListener((_: ActionEvent) => {
    println("Summoning your waifu...")
    new ProcessBuilder(
      "mpv",
      videoList.getSelectedValue.getAbsolutePath,
      "--audio-file=" + audioList.getSelectedValue.getAbsolutePath,
      "--sub-file=" + subList.getSelectedValue.getAbsolutePath,
    ).redirectOutput(ProcessBuilder.Redirect.INHERIT).redirectError(ProcessBuilder.Redirect.INHERIT).start
  })

  val textArea = new JTextArea()
  textArea.setBackground(Color.BLACK)
  textArea.setForeground(Color.LIGHT_GRAY)
  textArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12))

  val consoleScrollPane = new JScrollPane
  consoleScrollPane.add(textArea)
  consoleScrollPane.setViewportView(textArea)

  System.setOut(new PrintStream(DualOutputStream(false)))
  System.setErr(new PrintStream(DualOutputStream(true)))

  val frame = new EventJFrame("AnimeTool [mpv]")
  frame.addListeners()
  //mdlaf.MaterialLookAndFeel.changeTheme(new MaterialLiteTheme)

  frame.setSize(800, 600)
  frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE)
  val panel = new JPanel

  frame.add(panel)

  placeComponents(panel)

  frame.setVisible(true)
  frame.addComponentListener(ResizeListener())

  println("AnimeTool init")


  private def makeIcon(icon : FontAwesome, element: Any): Icon = {

    val sz = element match {
      case _: JButton => theme.getButtonFont.getSize*1.5
      case _: JLabel =>  theme.getButtonFont.getSize
      case _ => theme.getButtonFont.getSize
    }
    IconFontSwing.buildIcon(icon,Math.round(sz), theme.getButtonTextColor)
  }

  case class ResizeListener() extends ComponentAdapter {
      override def componentResized(e : ComponentEvent): Unit = {
        panel.removeAll()
        placeComponents(panel)
    }
  }

  private def getBoundsInBounds(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeX = rectangle.width * offset / count
    val relativeY = 0

    val width = rectangle.width / count - paddingBetween
    val height = rectangle.height

    new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
  }

  private def getBoundsInBoundsV(offset: Int, count: Int, paddingBetween : Int, rectangle: Rectangle) : Rectangle = {
    val relativeY = rectangle.height * offset / count
    val relativeX = 0

    val actualPaddingBeween = if (offset + 1 == count ) 0 else paddingBetween

    val height = rectangle.height / count - actualPaddingBeween
    val width = rectangle.width

    new Rectangle(rectangle.x + relativeX, rectangle.y + relativeY, width, height)
  }

  private def getNextBounds(height: Int, verticalPadding : Int, rectangle: Rectangle): Rectangle = {
      val lastUnfuckMargin = 30
      val actualHeight =
        if (height < 0)
          (frame.getHeight - (rectangle.y + rectangle.height + verticalPadding)) - verticalPadding - lastUnfuckMargin
        else
          height

      new Rectangle(rectangle.x, rectangle.y + rectangle.height + verticalPadding, rectangle.width, actualHeight )
  }

  private def placeComponents(panel: JPanel): Unit = {
    panel.setLayout(null)

    val genericPaddingLeft = 5
    val verticalPadding = 5

    val startRect = new Rectangle(genericPaddingLeft, 0, frame.getWidth - genericPaddingLeft, 1)

    val playRect = getNextBounds(40,verticalPadding,startRect)

    playButton.setBounds(getBoundsInBounds(0,3,genericPaddingLeft,playRect))

    panel.add(playButton)

    val audioDelayContainerRect = getBoundsInBounds(1,3,genericPaddingLeft,playRect)




    audioDelayLabel.setBounds(getBoundsInBounds(0, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayLabel)
    //audioDelaySpinnerLabel.setBounds(getBoundsInBounds(1, 3,genericPaddingLeft,audioDelayContainerRect))
    //panel.add(audioDelaySpinnerLabel)
    audioDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,audioDelayContainerRect))
    panel.add(audioDelayText)

    val subDelayContainerRect = getBoundsInBounds(2,3,genericPaddingLeft,playRect)
    subDelayLabel.setBounds(getBoundsInBounds(0, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayLabel)
    subDelayText.setBounds(getBoundsInBounds(1, 2,genericPaddingLeft,subDelayContainerRect))
    panel.add(subDelayText)

    /*
    val cLabelsRect = getNextBounds(10,verticalPadding,playRect)
    clabel1.setBounds(getBoundsInBounds(0, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel1)

    clabel2.setBounds(getBoundsInBounds(1, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel2)

    clabel3.setBounds(getBoundsInBounds(2, 3, genericPaddingLeft, cLabelsRect))
    panel.add(clabel3)
    */

    val buttonsRect = getNextBounds(40,verticalPadding,playRect)
    val buttons1Rect = getBoundsInBounds(0, 3, genericPaddingLeft, buttonsRect)
    val buttons2Rect = getBoundsInBounds(1, 3, genericPaddingLeft, buttonsRect)
    val buttons3Rect = getBoundsInBounds(2, 3, genericPaddingLeft, buttonsRect)

    up1btn.setBounds(getBoundsInBounds(0,4,0, buttons1Rect))
    panel.add(up1btn)
    down1btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons1Rect))
    panel.add(down1btn)
    del1btn.setBounds(getBoundsInBounds(2,4,0, buttons1Rect))
    panel.add(del1btn)
    clear1btn.setBounds(getBoundsInBounds(3,4,0, buttons1Rect))
    panel.add(clear1btn)

    up2btn.setBounds(getBoundsInBounds(0,4,0, buttons2Rect))
    panel.add(up2btn)
    down2btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons2Rect))
    panel.add(down2btn)
    del2btn.setBounds(getBoundsInBounds(2,4,0, buttons2Rect))
    panel.add(del2btn)
    clear2btn.setBounds(getBoundsInBounds(3,4,0, buttons2Rect))
    panel.add(clear2btn)

    up3btn.setBounds(getBoundsInBounds(0,4,0, buttons3Rect))
    panel.add(up3btn)
    down3btn.setBounds(getBoundsInBounds(1,4,genericPaddingLeft, buttons3Rect))
    panel.add(down3btn)
    del3btn.setBounds(getBoundsInBounds(2,4,0, buttons3Rect))
    panel.add(del3btn)
    clear3btn.setBounds(getBoundsInBounds(3,4,0, buttons3Rect))
    panel.add(clear3btn)

    val consoleHeight = 80
    val listRect = getNextBounds(-1,verticalPadding,buttonsRect)
    listRect.height -= (consoleHeight + verticalPadding)

    videoListScrollPane.setBounds(getBoundsInBounds(0,3,genericPaddingLeft, listRect))
    panel.add(videoListScrollPane)

    audioListScrollPane.setBounds(getBoundsInBounds(1,3,genericPaddingLeft, listRect))
    panel.add(audioListScrollPane)

    subListScrollPane.setBounds(getBoundsInBounds(2,3,genericPaddingLeft, listRect))
    panel.add(subListScrollPane)

    val consoleRect = getNextBounds(-1, verticalPadding,listRect)
    consoleRect.width -= genericPaddingLeft
    consoleScrollPane.setBounds(consoleRect)
    panel.add(consoleScrollPane)


  }

  class SharedListSelectionHandler(list1: JList[ShortFile], list2: JList[ShortFile]) extends ListSelectionListener {
    override def valueChanged(e: ListSelectionEvent): Unit = {
      val lsm = e.getSource.asInstanceOf[ListSelectionModel]

      if (!lsm.isSelectionEmpty) {
        val minIndex = lsm.getMinSelectionIndex
        val i = minIndex
        println("Selected: " + i)
        if (list1.getModel.getSize - 1 < i)
          list1.clearSelection()
        else
          list1.setSelectedIndex(i)

        if (list2.getModel.getSize -1 < i)
          list2.clearSelection()
        else
          list2.setSelectedIndex(i)
      }
    }
  }

  class RightClickMouseAdapter(list: JList[ShortFile]) extends MouseAdapter {

    import javax.swing.JList
    import javax.swing.SwingUtilities

    override def mousePressed(e: MouseEvent): Unit = {
      if (SwingUtilities.isRightMouseButton(e)) list.getModel.asInstanceOf[SortableListModel[ShortFile]].remove(getRow(e.getPoint))
    }

    private def getRow(point: Point) = list.locationToIndex(point)
  }

  class ToggleSelectionModel(selfList: JList[ShortFile],list1: JList[ShortFile], list2: JList[ShortFile]) extends DefaultListSelectionModel() {

    private var frozen = false

    def freeze(freeze: Boolean) = this.frozen = freeze

    override def setSelectionInterval(index0: Int, index1: Int): Unit = {
      //println(index0)
      //println(index1)
        if (isSelectedIndex(index0)) {
          if (!frozen)
            super.removeSelectionInterval(index0, index1)
        } else {
            super.setSelectionInterval(index0, index1)
            if (!frozen) {
                if (list1.getModel.getSize - 1 < index0) {
                  list1.clearSelection()
                }
                else {
                  list1.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(true)
                  list1.setSelectedIndex(index0)
                  list1.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(false)
                }

              if (list2.getModel.getSize - 1 < index0) {
                list2.clearSelection()
              }
              else {
                list2.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(true)
                list2.setSelectedIndex(index0)
                list2.getSelectionModel.asInstanceOf[ToggleSelectionModel].freeze(false)
              }
            }
        }
    }
/*
    override def setValueIsAdjusting(isAdjusting: Boolean): Unit = {
      if (isAdjusting == false) gestureStarted = false
    }
    */
  }

  case class ClearActionListener(model: SortableListModel[ShortFile]) extends ActionListener {
      override def actionPerformed(e: ActionEvent): Unit = model.clear()
  }

  case class SortActionListener(list: JList[ShortFile], model: SortableListModel[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
       model.sort()
    }
  }

  case class UpActionListener(list: JList[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      val i =list.getSelectedIndex

      if (i == 0)
        return

      val model = list.getModel.asInstanceOf[SortableListModel[ShortFile]]

      val swap = model.get(i)
      model.set(i, model.get(i-1))
      model.set(i-1,swap)

      list.setSelectedIndex(i-1)
    }
  }

  case class DownActionListener(list: JList[ShortFile]) extends ActionListener {
    override def actionPerformed(e: ActionEvent): Unit = {
      val i =list.getSelectedIndex

      if (i == list.getModel.getSize -1)
        return

      val model = list.getModel.asInstanceOf[SortableListModel[ShortFile]]

      val swap = model.get(i)
      model.set(i, model.get(i+1))
      model.set(i+1,swap)

      list.setSelectedIndex(i+1)
    }
  }

  class SortableListModel[ShortFile] extends DefaultListModel[ShortFile] {

    import java.util.Collections

    def sort(): Unit = {
      val list = new util.ArrayList[ShortFile]()
      for (i <- 0 until this.getSize) list.add(this.getElementAt(i))

      Collections.sort(list, (t: ShortFile, t1: ShortFile) => t.toString.compareTo(t1.toString))

      for (i <- 0 until list.size()) {
        this.set(i,list.get(i))
      }
    }
  }

  case class ShortFile(uri: URI) extends File(uri) {
    override def toString: String = this.getName
  }

  case class ListHandler(model: SortableListModel[ShortFile]) extends TransferHandler {
    override def canImport(support: TransferHandler.TransferSupport): Boolean
      = support.isDrop && support.isDataFlavorSupported(DataFlavor.stringFlavor)

    override def importData(support: TransferHandler.TransferSupport): Boolean =
      canImport(support) && {
      val transferable = support.getTransferable

      transferable.getTransferData(DataFlavor.stringFlavor) match {
        case line: String =>
          val data = line.split("[,\\s]")
          for (item <- data) {
            if (item.trim.nonEmpty) {
              val file = ShortFile(new URI(item.trim))
              if (file.exists && file.isFile) {
                model.add(model.getSize, file)
                println("Imported file: " + file.getAbsolutePath)
              }

            }
          }
          true
        case _ => false
      }
    }
  }

  case class DualOutputStream(error: Boolean) extends OutputStream {
      override def write(b: Int): Unit = {
        try {
          if (error)
            stderr.print(b.toChar)
          else
            stdout.print(b.toChar)
        } catch {
          case _ =>
        }

        try {
          textArea.append(String.valueOf(b.toChar))
          //textArea.setCaretPosition(textArea.getDocument.getLength)
          textArea.getCaret.setDot(Integer.MAX_VALUE)
        } catch {
          case _ =>
        }
      }
  }

  class EventJFrame(str: String) extends JFrame(str: String) with WindowListener with WindowFocusListener with WindowStateListener {

    import java.awt.Frame
    import java.awt.event.WindowEvent

    def displayStateMessage(prefix: String, e: WindowEvent): Unit = {
      val state = e.getNewState
      val oldState = e.getOldState
      val msg = prefix + "New state: " + convertStateToString(state) + "Old state: " + convertStateToString(oldState)
      println(msg)
    }

    import java.awt.Frame
    import java.awt.event.ActionEvent
    import java.awt.event.ActionListener
    import java.awt.event.WindowEvent

    import javax.swing.JScrollPane
    import javax.swing.JTextArea
    import java.awt.BorderLayout
    import java.awt.Dimension

    def addListeners(): Unit = {
      addWindowListener(this)
      addWindowFocusListener(this)
      addWindowStateListener(this)
      checkWM
    }

    def checkWM(): Unit = {
      val tk = frame.getToolkit
      if (!tk.isFrameStateSupported(Frame.ICONIFIED)) displayMessage("Your window manager doesn't support ICONIFIED.")
      else displayMessage("Your window manager supports ICONIFIED.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_VERT)) displayMessage("Your window manager doesn't support MAXIMIZED_VERT.")
      else displayMessage("Your window manager supports MAXIMIZED_VERT.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_HORIZ)) displayMessage("Your window manager doesn't support MAXIMIZED_HORIZ.")
      else displayMessage("Your window manager supports MAXIMIZED_HORIZ.")
      if (!tk.isFrameStateSupported(Frame.MAXIMIZED_BOTH)) displayMessage("Your window manager doesn't support MAXIMIZED_BOTH.")
      else displayMessage("Your window manager supports MAXIMIZED_BOTH.")
    }

    def windowClosing(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowClosing.")
      //A pause so user can see the message before
      //the window actually closes.
      val task = new ActionListener() {
        var alreadyDisposed = false
        override

        def actionPerformed(e: ActionEvent): Unit = {
          if (frame.isDisplayable) {
            alreadyDisposed = true
            frame.dispose
          }
        }
      }

    }

    def windowClosed(e: WindowEvent): Unit = { //This will only be seen on standard output.
      displayMessage("WindowListener method called: windowClosed.")
    }

    def windowOpened(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowOpened.")
    }

    def windowIconified(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowIconified.")
    }

    def windowDeiconified(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowDeiconified.")
    }

    def windowActivated(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowActivated.")
    }

    def windowDeactivated(e: WindowEvent): Unit = {
      displayMessage("WindowListener method called: windowDeactivated.")
    }

    def windowGainedFocus(e: WindowEvent): Unit = {
      displayMessage("WindowFocusListener method called: windowGainedFocus.")
    }

    def windowLostFocus(e: WindowEvent): Unit = {
      displayMessage("WindowFocusListener method called: windowLostFocus.")
    }

    def windowStateChanged(e: WindowEvent): Unit = {
      displayStateMessage("WindowStateListener method called: windowStateChanged.", e)
      placeComponents(panel)
    }

    def displayMessage(msg: String): Unit = {
      //display.append(msg + newline)
      println(msg)
    }

    def convertStateToString(state: Int): String = {
      if (state == Frame.NORMAL) return "NORMAL"
      var strState = " "
      if ((state & Frame.ICONIFIED) != 0) strState += "ICONIFIED"
      //MAXIMIZED_BOTH is a concatenation of two bits, so
      //we need to test for an exact match.
      if ((state & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH) strState += "MAXIMIZED_BOTH"
      else {
        if ((state & Frame.MAXIMIZED_VERT) != 0) strState += "MAXIMIZED_VERT"
        if ((state & Frame.MAXIMIZED_HORIZ) != 0) strState += "MAXIMIZED_HORIZ"
        if (" " == strState) strState = "UNKNOWN"
      }
      strState.trim
    }
  }

}
