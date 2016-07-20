/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package lstm.main;
import lstm.io.leadsheet.LeadSheetDataSequence;
/**
 *
 * @author cssummer16
 */
public abstract class AutoEncoderTest {
    
    private LeadSheetDataSequence inputSequence;
    private LeadSheetDataSequence decoderInputSequence;
    private LeadSheetDataSequence outputSequence;
    
    public AutoEncoderTest(LeadSheetDataSequence inputSequence, LeadSheetDataSequence decoderInputSequence, LeadSheetDataSequence outputSequence)
    {
        
    }
}
