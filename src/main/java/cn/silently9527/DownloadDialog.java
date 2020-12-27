package cn.silently9527;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.ValidationInfo;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class DownloadDialog extends DialogWrapper implements ActionListener {

    private JTextField downloadUrlField;
    private JTextField fileDirField;
    private JTextField threadNumField;

    private JFileChooser chooser = new JFileChooser();

    public DownloadDialog() {
        super(true); // use current window as parent
        init();
        setTitle("File Fast Download");
        setSize(300, 300);
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY); // 设定只能选择到文件夹
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        Box verticalBox = Box.createVerticalBox();
        verticalBox.add(createUrlBox());
        verticalBox.add(Box.createVerticalStrut(10));
        verticalBox.add(createFileDirJPanel());
        verticalBox.add(Box.createVerticalStrut(10));
        verticalBox.add(createThreadNumJPanel());
        return verticalBox;
    }

    private Box createUrlBox() {
        Box horizontalBox = Box.createHorizontalBox();
        JLabel label = new JLabel("文件下载地址:");
        horizontalBox.add(label);
        horizontalBox.add(Box.createHorizontalStrut(10));
        downloadUrlField = new JTextField(20);
        horizontalBox.add(downloadUrlField);
        return horizontalBox;
    }

    private Box createFileDirJPanel() {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(new JLabel("文件保存目录:"));
        horizontalBox.add(Box.createHorizontalStrut(10));

        fileDirField = new JTextField(15);
        horizontalBox.add(fileDirField);
        horizontalBox.add(Box.createHorizontalStrut(10));

        JButton button = new JButton("浏览");
        horizontalBox.add(button);
        button.addActionListener(this);
        return horizontalBox;
    }


    private Box createThreadNumJPanel() {
        Box horizontalBox = Box.createHorizontalBox();
        horizontalBox.add(new JLabel("下载线程数:"));
        horizontalBox.add(Box.createHorizontalStrut(22));

        threadNumField = new JTextField("10", 5);
        horizontalBox.add(threadNumField);
        return horizontalBox;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int state = chooser.showOpenDialog(null);// 此句是打开文件选择器界面的触发语句
        if (state == 1) {
            return;
        }
        File f = chooser.getSelectedFile();// f为选择到的目录
        fileDirField.setText(f.getAbsolutePath());
    }

    @Nullable
    @Override
    protected ValidationInfo doValidate() {
        if (StringUtils.isBlank(downloadUrlField.getText())) {
            return new ValidationInfo("文件下载地址必填");
        }
        if (StringUtils.isBlank(fileDirField.getText())) {
            return new ValidationInfo("文件保存目录必填");
        }
        if (StringUtils.isBlank(threadNumField.getText())) {
            return new ValidationInfo("下载线程数必填");
        }
        return null;
    }

    public String getDownloadURL() {
        return downloadUrlField.getText();
    }

    public String getDownloadDir() {
        return fileDirField.getText();
    }

    public Integer getThreadNum() {
        return Integer.valueOf(threadNumField.getText());
    }
}
