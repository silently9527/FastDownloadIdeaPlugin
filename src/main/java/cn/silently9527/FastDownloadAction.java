package cn.silently9527;

import cn.silently9527.download.download.Downloader;
import cn.silently9527.download.download.MultiThreadFileDownloader;
import cn.silently9527.download.support.MultiThreadDownloadProgressPrinter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class FastDownloadAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getData(PlatformDataKeys.PROJECT);

        DownloadDialog downloadDialog = new DownloadDialog();
        if (downloadDialog.showAndGet()) {
            // user pressed OK
            try {
                startDownloadTask(project, downloadDialog.getDownloadURL(),
                        downloadDialog.getDownloadDir(), downloadDialog.getThreadNum());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

    }

    private void startDownloadTask(Project project, String downloadURL, String downloadDir, Integer threadNum) throws IOException {
        MultiThreadDownloadProgressPrinter downloadProgressPrinter = new MultiThreadDownloadProgressPrinter(threadNum);
        ProgressManager.getInstance().run(new Task.Backgroundable(project, "File Downloading") {
            private long tmpAlreadyDownloadLength;
            private long speed;

            public void run(@NotNull ProgressIndicator progressIndicator) {
                // start your process
                while (true) {
                    long alreadyDownloadLength = downloadProgressPrinter.getAlreadyDownloadLength();
                    long contentLength = downloadProgressPrinter.getContentLength();
                    if (alreadyDownloadLength != 0 && alreadyDownloadLength >= contentLength) {
                        // Finished
                        progressIndicator.setFraction(1.0);
                        progressIndicator.setText("finished");
                        break;
                    }
                    setProgressIndicator(progressIndicator, contentLength, alreadyDownloadLength);
                    sleep();
                }
            }

            private void setProgressIndicator(ProgressIndicator progressIndicator, long contentLength,
                                              long alreadyDownloadLength) {
                if (alreadyDownloadLength == 0 || contentLength == 0) {
                    return;
                }
                speed = alreadyDownloadLength - tmpAlreadyDownloadLength;
                tmpAlreadyDownloadLength = alreadyDownloadLength;

                System.out.println(contentLength + " => " + alreadyDownloadLength);
                double value = (double) alreadyDownloadLength / (double) contentLength;
                System.out.println(String.format("%.2f", value));

                double fraction = Double.parseDouble(String.format("%.2f", value));
                progressIndicator.setFraction(fraction);
                String text = "already download " + fraction * 100 + "% ,speed: " + (speed / 1000) + "KB";
                progressIndicator.setText(text);
                System.out.println(text);
            }
        });

        CompletableFuture.runAsync(() -> {
            try {
                Downloader downloader = new MultiThreadFileDownloader(threadNum, downloadProgressPrinter);
                downloader.download(downloadURL, downloadDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).exceptionally(e -> {
            throw new RuntimeException(e);
        });
    }

    private void sleep() {
        try {
            Thread.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
