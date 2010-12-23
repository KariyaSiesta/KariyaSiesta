package org.sapid.checker.cx.command;

public class SimpleCommandOutput implements CommandOutput {

    /**
     * コマンドの実行結果
     */
    private StringBuilder output = new StringBuilder();
    
    @Override
    public String hook(String buffer) {
        output.append(buffer);
        return buffer;
    }
    
    public String getOutput() {
        return output.toString();
    }

}
