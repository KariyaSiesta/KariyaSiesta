package org.sapid.checker.cx.command;

public class SimpleCommandOutput implements CommandOutput {

    /**
     * ���ޥ�ɤμ¹Է��
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
